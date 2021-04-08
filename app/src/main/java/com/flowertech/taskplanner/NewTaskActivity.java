package com.flowertech.taskplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private TextView mTextViewReminder;
    private LinearLayout mLinearLayoutToDoList;
    private NewTaskViewModel mNewTaskViewModel;
    private Task task;
    private final List<ToDoList> toDoList = new ArrayList<>();
    private Context context;
    NotificationProvider notificationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        context = this;

        mNewTaskViewModel = new ViewModelProvider(this).get(NewTaskViewModel.class);
        CalendarsProvider calendarsProvider = new CalendarsProvider();
        notificationProvider = new NotificationProvider();

        task = new Task();

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        Spinner mSpinnerCategory = findViewById(R.id.spinner_category);
        mTextViewReminder = findViewById(R.id.edit_text_reminder);
        Button mClearDueDate = findViewById(R.id.due_date_clear);
        Button mClearReminder = findViewById(R.id.reminder_clear);
        mLinearLayoutToDoList = findViewById(R.id.to_do_list_view);
        ImageButton mImageButtonAddToDO = findViewById(R.id.add_to_do);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);

        setTitle(R.string.activity_new_task_title);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                calendarsProvider.setDueDate(this, task, mTextViewDueDate, mTextViewReminder, NewTaskActivity.this)
        );

        //when clicked on mTextViewReminder, invoke datetime picker and setText to mTextViewReminder
        mTextViewReminder.setOnClickListener(v ->
                calendarsProvider.setReminder(this, task, mTextViewReminder, NewTaskActivity.this)
        );

        //spinner category
        mSpinnerCategory.setOnItemSelectedListener(this);

        CategorySpinner categorySpinner = new CategorySpinner();
        categorySpinner.createSpinner(mNewTaskViewModel, this, task, mSpinnerCategory, this);

        //when clicked on mClearDueDate, clear due date
        mClearDueDate.setOnClickListener(v -> {
            task.dueDate = null;
            task.reminder = null;
            mTextViewDueDate.setText(R.string.hint_date);
            mTextViewReminder.setText(R.string.hint_date);
        });

        //when clicked on mClearReminder, clear reminder date
        mClearReminder.setOnClickListener(v -> {
            task.reminder = null;
            mTextViewReminder.setText(R.string.hint_date);
        });

        //add To Do into List through dialog
        mImageButtonAddToDO.setOnClickListener(addOnClickListener);
    }

    private void addTask() {
        //validate inputs
        if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
                TextUtils.isEmpty(mEditTextDescription.getText()) &&
                TextUtils.isEmpty(mTextViewDueDate.getText())) {
            Toast.makeText(
                    this,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show();
        } else {
            task.title = mEditTextTitle.getText().toString();
            task.description = mEditTextDescription.getText().toString();
            task.state = State.created;

            if (task.title.length() == 0){
                Toast.makeText(getApplicationContext(),
                        R.string.new_task_no_title,
                        Toast.LENGTH_LONG).show();
                return;
            }

            AppDatabase.databaseWriteExecutor.execute(() -> {
                task.id = mNewTaskViewModel.insert(task);
                if (task.reminder != null){
                    long difference = task.reminder.toInstant().toEpochMilli() - System.currentTimeMillis();
                    notificationProvider.scheduleNotification(this, difference, task);
                }
                if (toDoList != null){
                    for (int i = 0; i < toDoList.size(); i++){
                        ToDoList toDo;
                        toDo = toDoList.get(i);
                        toDo.taskListId = task.id;
                        mNewTaskViewModel.insert(toDo);
                    }
                }
            });
        }
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Category category = (Category) parent.getSelectedItem();
        if (position == 0){
            task.categoryId = null;
        } else {
            task.categoryId = category.id;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_task) {
            addTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showToDoList(){
        //inflate Linear Layout
        LayoutInflater inflater = getLayoutInflater();
        mLinearLayoutToDoList.removeAllViews();

        for (int i = 0; i < toDoList.size(); i++) {

            View view = inflater.inflate(R.layout.to_do_list_item, mLinearLayoutToDoList, false);
            CheckBox checkBox = view.findViewById(R.id.check_to_do);
            checkBox.setText(toDoList.get(i).description);
            if (toDoList.get(i).checked){
                checkBox.setChecked(true);
                checkBox.setBackgroundColor(getResources().getColor(R.color.pastel_green));
            }
            else {
                checkBox.setChecked(false);
                checkBox.setBackgroundColor(getResources().getColor(R.color.system_white));
            }

            mLinearLayoutToDoList.addView(view);

            view.setTag(toDoList.get(i));

            checkBox.setOnCheckedChangeListener(colorOnClickListener);

            ImageButton mRemoveToDo = view.findViewById(R.id.remove_to_do);
            //Remove to do
            mRemoveToDo.setOnClickListener(deleteOnClickListener);

            ImageButton mEditToDo = view.findViewById(R.id.edit_to_do);
            //Edit to do
            mEditToDo.setOnClickListener(updateOnClickListener);
        }
    }

    CompoundButton.OnCheckedChangeListener colorOnClickListener = (buttonView, isChecked) -> {
        View itemView = (View) buttonView.getParent();
        ToDoList toDo = (ToDoList) itemView.getTag();
        int index = toDoList.indexOf(toDo);
        toDo.checked = buttonView.isChecked();
        toDoList.set(index, toDo);
        showToDoList();
    };

    View.OnClickListener addOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.add_to_do);

            // Set up the input
            final EditText input = new EditText(context);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.add, (dialog, which) -> {
                ToDoList toDo = new ToDoList();
                String description = input.getText().toString();
                if (description.length() != 0) {
                    toDo.description = description;
                    toDo.checked = false;
                    toDo.order = toDoList.size();
                    toDoList.add(toDo);
                    showToDoList();
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    };

    View.OnClickListener deleteOnClickListener = v -> {
        View itemView = (View) v.getParent();
        ToDoList toDo = (ToDoList) itemView.getTag();
        toDoList.remove(toDo);
       showToDoList();
    };

    View.OnClickListener updateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View itemView = (View) v.getParent();
            ToDoList toDo = (ToDoList) itemView.getTag();
            int index = toDoList.indexOf(toDo);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.edit_to_do);

            // Set up the input
            final EditText input = new EditText(context);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            input.setText(toDo.description);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton(R.string.save, (dialog, which) -> {
                String description = input.getText().toString();
                if (description.length() != 0)
                    toDo.description = description;

                toDoList.set(index, toDo);
                showToDoList();
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    };
}