package ru.buylist.data.dao

import androidx.room.*
import ru.buylist.data.entity.Category

@Dao
interface CategoryDao {

    @Insert
    fun insertCategory(category: Category)

    @Update
    fun updateCategory(category: Category)

    @Delete
    fun deleteCategory(category: Category)

    @Delete
    fun deleteSelectedCategory(categories: MutableList<Category>)

    @Query("DELETE FROM categories")
    fun deleteAllCategories()

    @Query("SELECT * FROM categories")
    fun getCategories(): MutableList<Category>

    @Query("SELECT * FROM categories WHERE name = :categoryName")
    fun getCategory(categoryName: String)

    @Query("UPDATE categories SET color = :categoryColor")
    fun updateCategoryColor(categoryColor: String)

}