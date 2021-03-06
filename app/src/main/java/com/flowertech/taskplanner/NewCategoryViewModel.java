package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class NewCategoryViewModel extends AndroidViewModel {

    private final CategoryRepository mCategoryRepository;

    public NewCategoryViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
    }

    public void insert(Category category) {
        mCategoryRepository.insert(category);
    }
}
