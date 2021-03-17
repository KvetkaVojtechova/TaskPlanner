package com.flowertech.taskplanner;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

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
    NotificationProvider notificationProvider;

    private Task task;
    private Date originalReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

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

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);
        setTitle(R.string.activity_edit_task_title);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v -> calendarsProvider.setDueDate(this, task, mTextViewDueDate, mTextViewReminder, EditTaskActivity.this));

        //when clicked on mTextViewReminder, invoke datetime picker and setText to mTextViewReminder
        mTextViewReminder.setOnClickListener(v -> calendarsProvider.setReminder(this, task, mTextViewReminder, EditTaskActivity.this));

        //spinner category
        mSpinnerCategory.setOnItemSelectedListener(this);

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

            if (task.reminder != null || task.reminder != originalReminder){
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

    private void switchStateColor(State state){
        if (state == State.created){
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (state == State.inProgress){
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            mImageStateClosed.setColorFilter(ContextCompat.getColor(this, R.color.blueGray), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateCreated.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
            mImageStateInProgress.setColorFilter(ContextCompat.getColor(this, R.color.black_button), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}
