package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NewTaskViewModel extends AndroidViewModel implements CategoriesProvider{

    private final TaskRepository mTaskRepository;
    private final ToDoListRepository mToDoListRepository;
    private final LiveData<List<Category>> mAllCategories;

    public NewTaskViewModel(@NonNull Application application) {
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

    public Long insert(Task task) {
        return mTaskRepository.insert(task);
    }
}
