package com.flowertech.taskplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;


class TaskRepository {
    private final TaskDao mTaskDao;
    private final LiveData<List<Task>> mAllTasks;
    private final LiveData<List<TaskList>> mAllTaskList;

    TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAll();
        mAllTaskList = mTaskDao.getTaskList();
    }

    LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    LiveData<List<TaskList>> getAllTaskList() {return mAllTaskList;}

    LiveData<List<TaskList>> filterTaskList(boolean isCreated, boolean isInProgress, boolean isClosed) {

        LiveData<List<TaskList>> filteredTaskList;
        String query = "SELECT Tasks.Id, Tasks.Title, Tasks.Description, Tasks.due_date," +
                " Tasks.State, Tasks.category_id, Categories.abbreviation " +
                "FROM Tasks LEFT JOIN Categories ON Tasks.category_id = Categories.Id";

        if (isCreated && !isInProgress && !isClosed)
            query += " WHERE Tasks.State = 0";
        else if (!isCreated && isInProgress && !isClosed)
            query += " WHERE Tasks.State = 1";
        else if (!isCreated && !isInProgress && isClosed)
            query += " WHERE Tasks.State = 2";
        else if (isCreated && isInProgress && !isClosed)
            query += " WHERE Tasks.State = 0 OR Tasks.State = 1";
        else if (isCreated && !isInProgress && isClosed)
            query += " WHERE Tasks.State = 0 OR Tasks.State = 2";
        else if (!isCreated && isInProgress && isClosed)
            query += " WHERE Tasks.State = 1 OR Tasks.State = 2";

        filteredTaskList = mTaskDao.filterTasks(new SimpleSQLiteQuery(query));

        return filteredTaskList;

    }

    LiveData<Task> getTask(Long id) {
        return mTaskDao.getTask(id);
    }

    Long insert(Task task) {
        return mTaskDao.insert(task);
    }

    void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> mTaskDao.update(task));
    }

    void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> mTaskDao.delete(task));
    }

    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> mTaskDao.deleteAll());
    }
}
