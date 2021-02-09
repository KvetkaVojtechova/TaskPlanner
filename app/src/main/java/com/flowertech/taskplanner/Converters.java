package com.flowertech.taskplanner;

import androidx.room.TypeConverter;

import java.util.Date;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static State fromState(Integer value) {
        return value == null ? null : State.values()[value];
    }

    @TypeConverter
    public static Integer toState(State state) {
        return state == null ? null : state.ordinal();
    }

}
