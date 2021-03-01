package com.flowertech.taskplanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insert(Category category);

    @Delete
    void delete(Category category);

    @Update
    void update(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getAll();

    @Query("DELETE  FROM categories")
    void deleteAll();
}
