package com.flowertech.taskplanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class NewTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK =
            "com.flowertech.tasklistsql.EXTRA_TASK";

    private EditText mEditTextTitle;
    private EditText mEditTextDescription;
    private TextView mTextViewDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        mEditTextTitle = findViewById(R.id.edit_text_title);
        mEditTextDescription = findViewById(R.id.edit_text_description);
        mTextViewDueDate = findViewById(R.id.edit_text_date);
        final Button button = findViewById(R.id.button_save);

        //when clicked on mTextViewDueDate, invoke datetime picker and setText to mTextViewDueDate
        mTextViewDueDate.setOnClickListener(v ->
                new DateTimePicker().invoke(
                        NewTaskActivity.this,
                        (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) ->
                                mTextViewDueDate.setText(selectedDay + "." + selectedMonth + "." + selectedYear + " " + selectedHour + ":" + selectedMinute)
                )
        );

        //when button is clicked validate data and setResult
        button.setOnClickListener(view -> {
            scheduleNotification(this, 5000, 5);
//            Intent replyIntent = new Intent();
//
//            if(TextUtils.isEmpty(mEditTextTitle.getText()) &&
//                    TextUtils.isEmpty(mEditTextDescription.getText()) &&
//                    TextUtils.isEmpty(mTextViewDueDate.getText())) {
//                setResult(RESULT_CANCELED, replyIntent);
//            } else {
//                Task task = new Task();
//                task.title = mEditTextTitle.getText().toString();
//                task.description = mEditTextDescription.getText().toString();
//                task.state = State.created;
//
//                if (task.title.length() == 0){
//                    setResult(RESULT_CANCELED, replyIntent);
//                    Toast.makeText(getApplicationContext(),
//                            R.string.new_task_no_title,
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                Date dueDate = DateConverters.StringToDate(mTextViewDueDate.getText().toString());
//                /*if (dueDate == null){
//                    setResult(RESULT_CANCELED, replyIntent);
//                    Toast.makeText(getApplicationContext(),
//                            R.string.new_task_wrong_date,
//                            Toast.LENGTH_LONG).show();
//                    return;
//                }*/
//
//                task.dueDate = dueDate;
//
//                Bundle bundle = new Bundle();
//                bundle.putSerializable(EXTRA_TASK, task);
//                replyIntent.putExtras(bundle);
//
//                setResult(RESULT_OK, replyIntent);
//            }
//            finish();
        });
    }

    public void scheduleNotification(Context context, long delay, int notificationId) {//delay is after how much time(in millis) from current time you want to schedule the notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_add_24)
            .setContentTitle("My notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(activity);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Notification notification = builder.build();
        notificationManager.notify(notificationId, notification);

    }
}