package com.flowertech.taskplanner;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

@Dao
public interface ToDoListDao {
    @Insert
    void insert(ToDoList toDoList);

    @Delete
    void delete(ToDoList toDoList);

    @Update
    void update(ToDoList toDoList);
}
