package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EditTaskViewModel extends AndroidViewModel {

    private CategoryRepository mCategoryRepository;
    private final LiveData<List<Category>> mAllCategories;

    public EditTaskViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
        mAllCategories = mCategoryRepository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }
}
