package com.flowertech.taskplanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ToDoListDao {
    @Insert
    void insert(ToDoList toDoList);

    @Delete
    void delete(ToDoList toDoList);

    @Update
    void update(ToDoList toDoList);

    @Query("SELECT * FROM toDoLists WHERE task_list_id = :id")
    LiveData<List<ToDoList>> getAll(Long id);

    @Query("DELETE  FROM toDoLists")
    void deleteAll();
}
