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

import java.util.Calendar;
import java.util.Formatter;

public class NewTaskActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;
    private TextView mTextViewReminder;
    private Spinner mSpinnerCategory;
    private NewTaskViewModel mNewTaskViewModel;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        mNewTaskViewModel = new ViewModelProvider(this).get(NewTaskViewModel.class);

        task = new Task();

        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        mSpinnerCategory = findViewById(R.id.spinner_category);
        mTextViewReminder = findViewById(R.id.edit_text_reminder);

        //menu back
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_back_24);

        setTitle(R.string.activity_new_task_title);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        NewTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) -> {
                            Calendar c = Calendar.getInstance();
                            c.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                            task.dueDate = c.getTime();
                            mTextViewDueDate.setText(DateConverters.DateToString(task.dueDate));
                        }
                )
        );

        //when clicked on mTextViewReminder, invoke datetime picker and setText to mTextViewReminder
        mTextViewReminder.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        NewTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) -> {

                            Calendar c = Calendar.getInstance();
                            c.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                            task.reminder = c.getTime();
                            mTextViewReminder.setText(DateConverters.DateToString(task.reminder));
                        }

                )
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

            if (task.reminder != null){
                Long difference = task.reminder.toInstant().toEpochMilli() - System.currentTimeMillis();
                scheduleNotification(this, difference, (int)task.id);
            }

            mNewTaskViewModel.insert(task);
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
        switch (item.getItemId()) {
            case R.id.add_task:
                addTask();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void scheduleNotification(Context context, long delay, int notificationId) {
        //delay is after how much time(in millis) from current time you want to schedule the notification

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_task_planner)
                .setContentTitle(task.title)
                .setContentText(String.format(getResources().getString(R.string.reminder_text), task.title, DateConverters.DateToString(task.dueDate)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, ReminderNotificationPublisher.class);
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, ReminderNotificationPublisher.class);
        notificationIntent.putExtra(ReminderNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(ReminderNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

}