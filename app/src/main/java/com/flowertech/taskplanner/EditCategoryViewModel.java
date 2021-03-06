package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class EditCategoryViewModel extends AndroidViewModel {

    private final CategoryRepository mCategoryRepository;

    public EditCategoryViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
    }

    public void update(Category category) {
        mCategoryRepository.update(category);
    }
}
