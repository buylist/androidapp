package ru.buylist.data.dao

import androidx.room.*
import ru.buylist.data.entity.Recipe

@Dao
interface RecipeDao {

    @Insert
    fun insertRecipe(recipe: Recipe)

    @Update
    fun updateRecipe(recipe: Recipe)

    @Delete
    fun deleteRecipe(recipe: Recipe)

    @Delete
    fun deleteSelectedRecipes(recipes: List<Recipe>)

    @Query("DELETE FROM recipes")
    fun deleteAllRecipes()

    @Query("SELECT * FROM recipes")
    fun getRecipes(): List<Recipe>

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipe(recipeId: Long): Recipe

}