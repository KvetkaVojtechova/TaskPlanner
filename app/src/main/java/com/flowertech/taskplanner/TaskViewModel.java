package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository mTaskRepository;
    private final LiveData<List<TaskEntity>> mAllTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        mAllTasks = mTaskRepository.getAllTasks();
    }

    LiveData<List<TaskEntity>> getAllTasks() {
        return mAllTasks;
    }

    public void insert(TaskEntity taskEntity) {

        mTaskRepository.insert(taskEntity);
    }

    public void update(TaskEntity taskEntity) {
        mTaskRepository.update(taskEntity);
    }

    public void delete(TaskEntity taskEntity) {
        mTaskRepository.delete(taskEntity);
    }

    public void deleteAllNotes() {
        mTaskRepository.deleteAll();
    }
}
