package com.flowertech.taskplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;


class TaskRepository {
    private TaskDao mTaskDao;
    private LiveData<List<TaskEntity>> mAllTasks;

    TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mTaskDao = db.taskDao();
        mAllTasks = mTaskDao.getAll();
    }

    LiveData<List<TaskEntity>> getAllTasks() {
        return mAllTasks;
    }

    void insert(TaskEntity taskEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.insert(taskEntity);
        });
    }

    void update(TaskEntity taskEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.update(taskEntity);
        });
    }

    void delete(TaskEntity taskEntity) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.delete(taskEntity);
        });
    }

    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mTaskDao.deleteAll();
        });
    }
}
