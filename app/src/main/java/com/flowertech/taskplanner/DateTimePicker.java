package com.flowertech.taskplanner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

public class DateTimePicker {
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;

    public void invoke(Activity activity, OnDateTimePicked onPick, Date initialDate) {
        Calendar calendar = Calendar.getInstance();

        if (initialDate != null)
            calendar.setTime(initialDate);

        TimePickerDialog timePickerDialog = new TimePickerDialog(activity, (view, hourOfDay, minute) -> {
            selectedHour = hourOfDay;
            selectedMinute = minute;
            onPick.onDateTimePicked(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, (view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    interface OnDateTimePicked {
        void onDateTimePicked(int selectedYear, int selectedMonth, int selectedDay, int selectedHour, int selectedMinute);
    }
}
