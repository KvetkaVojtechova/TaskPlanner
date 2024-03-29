package com.flowertech.taskplanner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.List;

public class EditTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public static final String EDIT_TASK =
            "com.flowertech.tasklistsql.EDIT_TASK";

    private TextView mTextViewCreated;
    private TextView mTextViewInProgress;
    private TextView mTextViewFinished;
    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private TextView mTextViewReminder;
    private ImageView mImageStateCreated;
    private ImageView mImageStateInProgress;
    private ImageView mImageStateClosed;
    private Spinner mSpinnerCategory;
    private EditTaskViewModel mEditTaskViewModel;
    private LinearLayout mLinearLayoutToDoList;
    private ImageButton mRemoveToDo;
    private ImageButton mEditToDo;
    NotificationProvider notificationProvider;

    private Task task;
    private Date originalReminder;
    private List<ToDoList> toDoListList;
    private Context context;
    private int maxOrderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        context = this;

        mEditTaskViewModel = new ViewModelProvider(this).get(EditTaskViewModel.class);
        CalendarsProvider calendarsProvider = new CalendarsProvider();
        notificationProvider = new NotificationProvider();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Long id = bundle.getLong(EDIT_TASK);

        String created = getResources().getString(R.string.date_created);
        String inProgress = getResources().getString(R.string.date_in_progress);
        String closed = getResources().getString(R.string.date_closed);

        mTextViewCreated = findViewById(R.id.text_view_created);
        mTextViewInProgress = findViewById(R.id.text_view_in_progress);
        mTextViewFinished = findViewById(R.id.text_view_finished);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        mTextViewReminder = findViewById(R.id.edit_text_reminder);
        mImageStateCreated = findViewById(R.id.image_view_state_1);
        mImageStateInProgress = findViewById(R.id.image_view_state_2);
        mImageStateClosed = findViewById(R.id.image_view_state_3);
        mSpinnerCategory = findViewById(R.id.spinner_category);
        Button mClearDueDate = findViewById(R.id.due_date_clear);
        Button mClearReminder = findViewById(R.id.reminder_clear);
        mLinearLayoutToDoList = findViewById(R.id.to_do_list_view);
        ImageButton mImageButtonAddToDO = findViewById(R.id.add_to_do);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);
        setTitle(R.string.activity_edit_task_title);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v -> calendarsProvider.setDueDate(this, task, mTextViewDueDate, mTextViewReminder, EditTaskActivity.this));

        //when clicked on mTextViewReminder, invoke datetime picker and setText to mTextViewReminder
        mTextViewReminder.setOnClickListener(v -> calendarsProvider.setReminder(this, task, mTextViewReminder, EditTaskActivity.this));

        //spinner category
        mSpinnerCategory.setOnItemSelectedListener(this);

        //created image
        mImageStateCreated.setOnClickListener(v -> {
            task.state = State.created;
            switchStateColor(task.state);
            task.startDate = null;
            task.endDate = null;
        });

        //in progress image
        mImageStateInProgress.setOnClickListener(v -> {
            task.state = State.inProgress;
            switchStateColor(task.state);
            task.startDate = new Date();
            task.endDate = null;
        });

        //closed image
        mImageStateClosed.setOnClickListener(v -> {
            task.state = State.closed;
            switchStateColor(task.state);
            task.endDate = new Date();
        });

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

        //get task and insert it
        mEditTaskViewModel.getTask(id).observe(this, editTask -> {
            task = editTask;
            originalReminder = editTask.reminder;
            //fill out the fields with existing data
            mEditTextTitle.setText(task.title);
            mEditTextDescription.setText(task.description);
            if(task.dueDate != null)
                mTextViewDueDate.setText(DateConverters.DateToString(task.dueDate));
            if(task.reminder != null)
                mTextViewReminder.setText(DateConverters.DateToString(task.reminder));
            mTextViewCreated.setText(created + DateConverters.DateToString(task.created));
            if (task.startDate != null)
                mTextViewInProgress.setText(inProgress + DateConverters.DateToString(task.startDate));
            if (task.endDate != null)
                mTextViewFinished.setText(closed + DateConverters.DateToString(task.endDate));
            switchStateColor(task.state);


            CategorySpinner categorySpinner = new CategorySpinner();
            categorySpinner.createSpinner(mEditTaskViewModel, this, task, mSpinnerCategory, this);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                maxOrderNum = mEditTaskViewModel.getMaxOrderNum(task.id);
            });

            mEditTaskViewModel.getAllToDos(task.id).observe(this, toDoEntities -> {
                mLinearLayoutToDoList.removeAllViews();
                toDoListList = toDoEntities;
                //inflate Linear Layout
                LayoutInflater inflater = getLayoutInflater();

                for (int i = 0; i < toDoListList.size(); i++) {
                    View view = inflater.inflate(R.layout.to_do_list_item, mLinearLayoutToDoList, false);
                    CheckBox checkBox = view.findViewById(R.id.check_to_do);
                    checkBox.setText(toDoEntities.get(i).description);
                    if (toDoListList.get(i).checked){
                        checkBox.setChecked(true);
                        checkBox.setBackgroundColor(getResources().getColor(R.color.pastel_green));
                    }
                    else {
                        checkBox.setChecked(false);
                        checkBox.setBackgroundColor(getResources().getColor(R.color.system_white));
                    }

                    mLinearLayoutToDoList.addView(view);

                    view.setTag(toDoListList.get(i));

                    checkBox.setOnCheckedChangeListener(colorOnClickListener);

                    mRemoveToDo = view.findViewById(R.id.remove_to_do);
                    //Remove to do
                    mRemoveToDo.setOnClickListener(deleteOnClickListener);

                    mEditToDo = view.findViewById(R.id.edit_to_do);
                    //Edit to do
                    mEditToDo.setOnClickListener(updateOnClickListener);
                }
            });
        });
    }

    private void saveTask() {
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

            if (task.title.length() == 0){
                Toast.makeText(getApplicationContext(),
                        R.string.new_task_no_title,
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (task.reminder != originalReminder && originalReminder != null) {
                notificationProvider.cancelNotification(this, (int)task.id);
            }

            if (task.reminder != null && task.reminder != originalReminder){
                long difference = task.reminder.toInstant().toEpochMilli() - System.currentTimeMillis();
                notificationProvider.scheduleNotification(this, difference, task);
            }

            mEditTaskViewModel.update(task);
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
        menuInflater.inflate(R.menu.save_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_task) {
            saveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchStateColor(State state) {
        if (state == State.created) {
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (state == State.inProgress) {
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        }

    }

    CompoundButton.OnCheckedChangeListener colorOnClickListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            View itemView = (View) buttonView.getParent();
            ToDoList toDo = (ToDoList) itemView.getTag();
            toDo.checked = buttonView.isChecked();
            mEditTaskViewModel.update(toDo);
        }
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
                    toDo.taskListId = task.id;
                    maxOrderNum++;
                    toDo.order = maxOrderNum;
                    mEditTaskViewModel.insert(toDo);
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    };

    View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View itemView = (View) v.getParent();
            ToDoList toDo = (ToDoList) itemView.getTag();
            mEditTaskViewModel.delete(toDo);
        }
    };

    View.OnClickListener updateOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View itemView = (View) v.getParent();
            ToDoList toDo = (ToDoList) itemView.getTag();

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

                mEditTaskViewModel.update(toDo);
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        }
    };
}
