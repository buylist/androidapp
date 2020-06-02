package ru.buylist.data.repositories.recipe

import ru.buylist.data.entity.Recipe

interface RecipesDataSource {

    interface GetRecipeCallback {
        fun onRecipeLoaded(recipe: Recipe)
        fun onDataNotAvailable()
    }

    interface LoadRecipesCallback {
        fun onRecipesLoaded(recipes: List<Recipe>)
        fun onDataNotAvailable()
    }

    fun saveRecipe(recipe: Recipe)

    fun updateRecipe(recipe: Recipe)

    fun deleteRecipe(recipe: Recipe)

    fun deleteSelectedRecipes(recipes: List<Recipe>)

    fun deleteAllRecipes()

    fun getRecipes(callback: LoadRecipesCallback)

    fun getRecipe(recipeId: Long, callback: GetRecipeCallback)
}