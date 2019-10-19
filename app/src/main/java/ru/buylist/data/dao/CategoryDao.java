package ru.buylist.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
    LiveData<List<Category>> getAllCategories();
}
