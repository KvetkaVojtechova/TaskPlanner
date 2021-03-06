package com.flowertech.taskplanner;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface CategoriesProvider {
    LiveData<List<Category>> getAllCategories();
}
