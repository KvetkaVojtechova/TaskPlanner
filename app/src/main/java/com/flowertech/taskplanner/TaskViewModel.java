package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository mTaskRepository;
    private final LiveData<List<Task>> mAllTasks;
    private final LiveData<List<TaskList>> mAllTaskList;
    //filtering
    private final LiveData<List<TaskList>> mAllCreatedTaskList;
    private final LiveData<List<TaskList>> mAllInProgressTaskList;
    private final LiveData<List<TaskList>> mAllClosedTaskList;
    private final LiveData<List<TaskList>> mAllCreatedInProgressTaskList;
    private final LiveData<List<TaskList>> mAllCreatedClosedTaskList;
    private final LiveData<List<TaskList>> mAllInProgressClosedTaskList;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        mAllTasks = mTaskRepository.getAllTasks();
        mAllTaskList = mTaskRepository.getAllTaskList();
        //filtering
        mAllCreatedTaskList = mTaskRepository.getAllCreatedTaskList();
        mAllInProgressTaskList = mTaskRepository.getAllInProgressTaskList();
        mAllClosedTaskList = mTaskRepository.getAllClosedTaskList();
        mAllCreatedInProgressTaskList = mTaskRepository.getAllCreatedInProgressTaskList();
        mAllCreatedClosedTaskList = mTaskRepository.getAllCreatedClosedTaskList();
        mAllInProgressClosedTaskList = mTaskRepository.getAllInProgressClosedTaskList();
    }

    LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    LiveData<List<TaskList>> getAllTaskList() {return mAllTaskList;}

    LiveData<Task> getTask(Long id) {
        return mTaskRepository.getTask(id);
    }

    LiveData<List<TaskList>> filterTaskList(boolean isCreated, boolean isInProgress, boolean isClosed) {
        return mTaskRepository.filterTaskList(isCreated, isInProgress, isClosed);}

    LiveData<List<TaskList>> getAllCreatedTaskList() {return mAllCreatedTaskList;}

    LiveData<List<TaskList>> getAllInProgressTaskList() {return mAllInProgressTaskList;}

    LiveData<List<TaskList>> getAllClosedTaskList() {return mAllClosedTaskList;}

    LiveData<List<TaskList>> getAllCreatedInProgressTaskList() {return mAllCreatedInProgressTaskList;}

    LiveData<List<TaskList>> getAllCreatedClosedTaskList() {return mAllCreatedClosedTaskList;}

    LiveData<List<TaskList>> getAllInProgressClosedTaskList() {return mAllInProgressClosedTaskList;}


    //insert task into TaskRepository
    public void insert(Task task) {
        mTaskRepository.insert(task);
    }

    public void update(Task task) {
        mTaskRepository.update(task);
    }

    public void delete(Task task) {
        mTaskRepository.delete(task);
    }

    public void deleteAllTasks() {
        mTaskRepository.deleteAll();
    }
}
