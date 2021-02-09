package com.flowertech.taskplanner;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "tasks")
public class TaskEntity implements Serializable {

    public TaskEntity() {
        this.created = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull public Date created;

    @NonNull public String title;

    public String description;

    @NonNull public State state;

    @ColumnInfo(name = "start_date")
    public Date startDate;

    @ColumnInfo(name = "end_date")
    public Date endDate;

    @ColumnInfo(name = "category_id")
    public int categoryId;

    public Date reminder;

    @ColumnInfo(name = "due_date")
    public Date dueDate;
}

