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

}
