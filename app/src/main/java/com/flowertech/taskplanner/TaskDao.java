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
    void insert(TaskEntity tasks);

    @Delete
    void delete(TaskEntity task);

    @Update
    void update(TaskEntity tasks);

    @Query("SELECT * FROM tasks")
    LiveData<List<TaskEntity>> getAll();

    @Query("DELETE  FROM tasks")
    void deleteAll();
}
