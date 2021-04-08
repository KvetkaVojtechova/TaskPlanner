package com.flowertech.taskplanner;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.lifecycle.LifecycleOwner;

public class CategorySpinner {

    public CategorySpinner(){

    }

    public void createSpinner(CategoriesProvider categoriesProvider, Context context, Task task, Spinner spinnerCategory, LifecycleOwner lifecycleOwner) {

        categoriesProvider.getAllCategories().observe(lifecycleOwner, categoryEntities -> {

            if (categoryEntities == null || categoryEntities.size() == 0)
                return;
            if (!categoryEntities.get(0).abbr.equals("- - -")){
                Category emptyCategory = new Category();
                emptyCategory.abbr = "- - -";
                categoryEntities.add(0, emptyCategory);
            }

            // Creating adapter for spinner
            ArrayAdapter<Category> categoryAdapter =
                    new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categoryEntities);
            //find selected category
            Category selectedCategory = null;
            if (task.categoryId != null){
                selectedCategory = categoryEntities.stream()
                        .filter(category -> category.id == task.categoryId).findFirst().orElse(null);
            }
            // Drop down layout style - list view with radio button
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            spinnerCategory.setAdapter(categoryAdapter);
            //preselect category in spinner
            if (selectedCategory != null)
                spinnerCategory.setSelection(categoryAdapter.getPosition(selectedCategory));
        });
    }
}
