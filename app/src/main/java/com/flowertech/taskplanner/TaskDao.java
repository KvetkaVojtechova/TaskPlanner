package com.flowertech.taskplanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task tasks);

    @Delete
    void delete(Task task);

    @Update
    void update(Task tasks);

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAll();

    @Query("DELETE  FROM tasks")
    void deleteAll();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks INNER JOIN Categories ON Tasks.category_id = Categories.Id")
    LiveData<List<TaskList>> getTaskList();
}
