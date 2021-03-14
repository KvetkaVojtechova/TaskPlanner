package com.flowertech.taskplanner;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class CalendarsProvider {

    public void setDueDate(Context context, Task task, TextView textViewDueDate, TextView textViewReminder, Activity activity){
        new DateTimePicker().invoke(
                activity,
                (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                    if ((c.getTimeInMillis() - System.currentTimeMillis()) > 0){
                        task.dueDate = c.getTime();
                        textViewDueDate.setText(DateConverters.DateToString(task.dueDate));
                    } else {
                        Toast.makeText(
                                context,
                                R.string.wrong_date,
                                Toast.LENGTH_LONG).show();
                    }

                }, null
        );
        task.reminder = null;
        textViewReminder.setText(R.string.hint_date);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setReminder(Context context, Task task, TextView textViewReminder, Activity activity){
        if(task.dueDate == null){
            Toast.makeText(
                    context,
                    R.string.reminder_cannot_set,
                    Toast.LENGTH_LONG).show();
            return;
        }
        new DateTimePicker().invoke(
                activity,
                (selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                    if ((c.getTimeInMillis() - System.currentTimeMillis()) > 0){
                        if ((task.dueDate.toInstant().toEpochMilli() - c.getTimeInMillis()) >= 0){
                            task.reminder = c.getTime();
                            textViewReminder.setText(DateConverters.DateToString(task.reminder));
                        } else {
                            Toast.makeText(
                                    context,
                                    R.string.wrong_date_reminder,
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(
                                context,
                                R.string.wrong_date,
                                Toast.LENGTH_LONG).show();
                    }
                }, task.dueDate
        );
    }
}
