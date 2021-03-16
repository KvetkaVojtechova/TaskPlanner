package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class NewTaskViewModel extends AndroidViewModel implements CategoriesProvider{

    private TaskRepository mTaskRepository;
    private CategoryRepository mCategoryRepository;
    private final LiveData<List<Category>> mAllCategories;

    public NewTaskViewModel(@NonNull Application application) {
        super(application);
        mTaskRepository = new TaskRepository(application);
        mCategoryRepository = new CategoryRepository(application);
        mAllCategories = mCategoryRepository.getAllCategories();
    }

    public LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    public Long insert(Task task) {
        return mTaskRepository.insert(task);
    }
}
