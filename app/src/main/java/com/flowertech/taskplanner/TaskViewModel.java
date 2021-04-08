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

    public TaskViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        mAllTasks = mTaskRepository.getAllTasks();
        mAllTaskList = mTaskRepository.getAllTaskList();
    }

    LiveData<List<Task>> getAllTasks() {
        return mAllTasks;
    }

    LiveData<List<TaskList>> getAllTaskList() {return mAllTaskList;}

    LiveData<Task> getTask(Long id) {
        return mTaskRepository.getTask(id);
    }

    LiveData<List<TaskList>> filterTaskList(boolean isCreated, boolean isInProgress, boolean isClosed,
                                            boolean titleAsc, boolean titleDesc, boolean createdAsc,
                                            boolean createdDesc, boolean dueDateAsc, boolean dueDateDesc) {
        return mTaskRepository.filterTaskList(isCreated, isInProgress, isClosed, titleAsc, titleDesc,
                createdAsc, createdDesc, dueDateAsc, dueDateDesc);}

    //insert task into TaskRepository
    public void delete(Task task) {
        mTaskRepository.delete(task);
    }

    public void deleteAllTasks() {
        mTaskRepository.deleteAll();
    }
}
