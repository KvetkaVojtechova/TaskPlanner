package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EditTaskViewModel extends AndroidViewModel implements CategoriesProvider{

    private final TaskRepository mTaskRepository;
    private final ToDoListRepository mToDoListRepository;
    private final LiveData<List<Category>> mAllCategories;

    public EditTaskViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        CategoryRepository mCategoryRepository = new CategoryRepository(application);
        mToDoListRepository = new ToDoListRepository(application);
        mAllCategories = mCategoryRepository.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public LiveData<List<ToDoList>> getAllToDos(Long id) {return mToDoListRepository.getAllToDos(id);}

    LiveData<Task> getTask(Long id) {
        return mTaskRepository.getTask(id);
    }

    public void update(Task task) {
        mTaskRepository.update(task);
    }

    //Methods for to do list
    public void insert(ToDoList toDoList) {
        mToDoListRepository.insert(toDoList);
    }

    public void update(ToDoList toDoList) {
        mToDoListRepository.update(toDoList);
    }

    public void delete(ToDoList toDoList) {
        mToDoListRepository.delete(toDoList);
    }

    int getMaxOrderNum(Long id) {
        return mToDoListRepository.getMaxOrderNum(id);
    }
}
