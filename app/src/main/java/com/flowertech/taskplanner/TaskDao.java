package com.flowertech.taskplanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    Long insert(Task tasks);

    @Delete
    void delete(Task task);

    @Update
    void update(Task tasks);

    @Query("SELECT * FROM tasks")
    LiveData<List<Task>> getAll();

    @Query("DELETE  FROM tasks")
    void deleteAll();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id")
    LiveData<List<TaskList>> getTaskList();

    @Query("SELECT * FROM tasks WHERE id=:id")
    LiveData<Task> getTask(Long id);

    @RawQuery(observedEntities = Task.class)
    LiveData<List<TaskList>> filterTasks(SupportSQLiteQuery query);

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 0")
    LiveData<List<TaskList>> getCreatedTaskList();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 1")
    LiveData<List<TaskList>> getInProgressTaskList();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 2")
    LiveData<List<TaskList>> getClosedTaskList();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 0 OR Tasks.State = 1")
    LiveData<List<TaskList>> getCreatedInProgressTaskList();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 0 OR Tasks.State = 2")
    LiveData<List<TaskList>> getCreatedClosedTaskList();

    @Query("SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date, Tasks.State, Tasks.category_id, Categories.abbreviation " +
            "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id " +
            "WHERE Tasks.State = 1 OR Tasks.State = 2")
    LiveData<List<TaskList>> getInProgressClosedTaskList();
}
