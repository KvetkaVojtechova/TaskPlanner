package com.flowertech.taskplanner;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private TextView mTextViewReminder;
    private NewTaskViewModel mNewTaskViewModel;
    private Task task;
    NotificationProvider notificationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

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
        //ListView mListViewToDo = findViewById(R.id.to_do_list_view);

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


        /*mNewTaskViewModel.getAllToDos().observe(this, toDoEntities -> {
            // Creating adapter for list view
            ArrayAdapter<ToDoList> toDoListAdapter = new ArrayAdapter<ToDoList>(this, R.layout.to_do_list_item, toDoEntities);

            // attaching data adapter to list view
            mListViewToDo.setAdapter(toDoListAdapter);

        });*/

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
}