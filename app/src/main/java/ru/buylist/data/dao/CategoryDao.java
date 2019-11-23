package ru.buylist.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.buylist.data.entity.Category;

@Dao
public interface CategoryDao {

    @Insert
    void addCategory(Category category);

    @Delete
    void deleteCategory(Category category);

    @Update
    void updateCategory(Category category);

    @Query("SELECT * FROM categories WHERE name = :name")
    Category getCategory(String name);

    @Query("SELECT * FROM categories")
    List<Category> getAllCategories();

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> getLiveCategories();
}
