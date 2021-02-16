package com.flowertech.taskplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


class TaskRepository {
    private TaskDao mTaskDao;
    private LiveData<List<Task>> mAllTasks;

    TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAll();
    }

    LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    void insert(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(task);
        });
    }

    void update(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(task);
        });
    }

    void delete(Task task) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(task);
        });
    }

    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteAll();
        });
    }
}
