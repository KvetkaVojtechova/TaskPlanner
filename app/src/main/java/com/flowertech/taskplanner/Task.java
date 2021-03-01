package com.flowertech.taskplanner;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "tasks"/*,
        foreignKeys = {@ForeignKey(entity = Category.class,
        parentColumns = "id",
        childColumns = "category_id",
        onDelete = ForeignKey.NO_ACTION,
        onUpdate = ForeignKey.CASCADE)}*/)
public class Task implements Serializable {

    public Task() {
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

