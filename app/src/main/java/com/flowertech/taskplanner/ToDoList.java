package com.flowertech.taskplanner;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "toDoLists",
        foreignKeys = {@ForeignKey(entity = Task.class,
        parentColumns = "id",
        childColumns = "task_list_id",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE)})
public class ToDoList {

    public ToDoList() {
        this.created = new Date();
    }

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public Date created;

    @NonNull
    public String description;

    @NonNull
    public boolean checked;

    @ColumnInfo(name = "task_list_id")
    public Long taskListId;

    @NonNull
    public int order;
}
