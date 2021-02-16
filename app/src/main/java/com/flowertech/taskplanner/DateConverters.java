package com.flowertech.taskplanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverters {

    public static Date StringToDate(String str){

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date date = format.parse(str);
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String DateToString (Date date){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String dateTime = dateFormat.format(date);
        return dateTime;
    }

}
