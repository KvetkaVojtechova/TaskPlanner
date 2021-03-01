package com.flowertech.taskplanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryViewModel extends AndroidViewModel {

    private CategoryRepository mCategoryRepository;
    private final LiveData<List<Category>> mAllCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
        mAllCategories = mCategoryRepository.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    //insert category into CategoryRepository
    public void insert(Category category) {
        mCategoryRepository.insert(category);
    }

    public void update(Category category) {
        mCategoryRepository.update(category);
    }

    public void delete(Category category) {
        mCategoryRepository.delete(category);
    }

    public void deleteAllCategories() {
        mCategoryRepository.deleteAll();
    }
}
