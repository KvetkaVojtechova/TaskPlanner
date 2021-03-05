package com.flowertech.taskplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.Date;

public class TaskList {

    public long id;

    public String title;

    public String abbreviation;

    public String description;

    public State state;

    @ColumnInfo(name = "category_id")
    public Long categoryId;

    @ColumnInfo(name = "due_date")
    public Date dueDate;
}
