package com.flowertech.taskplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ToDoListRepository {

    private final ToDoListDao mToDoListDao;

    ToDoListRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mToDoListDao = db.toDoListDao();
    }

    LiveData<List<ToDoList>> getAllToDos(Long id) {
        return mToDoListDao.getAll(id);
    }

    void insert(ToDoList toDoList) {
        AppDatabase.databaseWriteExecutor.execute(() -> mToDoListDao.insert(toDoList));
    }

    void update(ToDoList toDoList) {
        AppDatabase.databaseWriteExecutor.execute(() -> mToDoListDao.update(toDoList));
    }

    void delete(ToDoList toDoList) {
        AppDatabase.databaseWriteExecutor.execute(() -> mToDoListDao.delete(toDoList));
    }

    int getMaxOrderNum(Long id) {
        return mToDoListDao.getMaxOrderNum(id);
    }

    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> mToDoListDao.deleteAll());
    }
}
