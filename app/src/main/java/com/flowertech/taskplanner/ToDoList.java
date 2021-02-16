package com.flowertech.taskplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "categories")
public class ToDoList {

    public ToDoList() {
        this.created = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public Date created;

    @NonNull
    public String description;

    @NonNull
    public boolean checked;

    @ColumnInfo(name = "task_list_id")
    public int taskListId;

}
