package com.flowertech.taskplanner;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CategoryRepository {

    private CategoryDao mCategoryDao;
    private LiveData<List<Category>> mAllCategories;

    CategoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mCategoryDao = db.categoryDao();
        mAllCategories = mCategoryDao.getAll();
    }

    LiveData<List<Category>> getAllCategories() {
        return mAllCategories;
    }

    void insert(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.insert(category);
        });
    }

    void update(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.update(category);
        });
    }

    void delete(Category category) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.delete(category);
        });
    }

    void deleteAll() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mCategoryDao.deleteAll();
        });
    }
}
