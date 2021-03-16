package com.flowertech.taskplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;

public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private TextView mTextViewReminder;
    private NewTaskViewModel mNewTaskViewModel;
    private Task task;
    NotificationProvider notificationProvider;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                Long id = mNewTaskViewModel.insert(task);
                task.id = id;
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_task) {
            addTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}