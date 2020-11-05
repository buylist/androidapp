package ru.buylist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.buylist.data.entity.Recipe

@Dao
interface RecipeDao {

    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteSelectedRecipes(recipes: List<Recipe>)

    @Query("SELECT * FROM recipes")
    fun observeRecipes(): LiveData<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun observeRecipeById(recipeId: Long): LiveData<Recipe>

    @Query("DELETE FROM recipes")
    suspend fun deleteAllRecipes()

    @Query("SELECT * FROM recipes")
    suspend fun getRecipes(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipe(recipeId: Long): Recipe

}