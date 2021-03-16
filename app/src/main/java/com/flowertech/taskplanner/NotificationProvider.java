package com.flowertech.taskplanner;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotificationProvider {

    public void scheduleNotification(AppCompatActivity activityC, long delay, Task task) {
        //delay is after how much time(in millis) from current time you want to schedule the notification

        //creates a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activityC, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_task_planner)
                .setContentTitle(task.title)
                .setContentText(String.format(activityC.getResources().getString(R.string.reminder_text), task.title, DateConverters.DateToString(task.dueDate)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Bundle bundle = new Bundle();
        bundle.putLong(EditTaskActivity.EDIT_TASK, task.id);

        Intent intent = new Intent(activityC, MainActivity.class);
        intent.putExtras(bundle);
        PendingIntent activity = PendingIntent.getActivity(activityC, (int)task.id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity).setAutoCancel(true);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(activityC, ReminderNotificationPublisher.class);
        notificationIntent.putExtra(ReminderNotificationPublisher.NOTIFICATION_ID, (int)task.id);
        notificationIntent.putExtra(ReminderNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activityC, (int)task.id, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) activityC.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    public void cancelNotification(Context context, int notificationId) {
        Intent notificationIntent = new Intent(context, ReminderNotificationPublisher.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
