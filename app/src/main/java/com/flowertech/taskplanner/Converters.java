package com.flowertech.taskplanner;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    //converts Long into Date
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    //converts Date into Long
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    //converts Integer into State
    @TypeConverter
    public static State fromState(Integer value) {
        return value == null ? null : State.values()[value];
    }

    //converts State into Long
    @TypeConverter
    public static Integer toState(State state) {
        return state == null ? null : state.ordinal();
    }

}
