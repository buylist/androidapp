package ru.buylist.data.repositories.recipe

import ru.buylist.data.dao.RecipeDao
import ru.buylist.data.entity.Recipe
import ru.buylist.utils.AppExecutors

class RecipesRepository private constructor(
        private val executors: AppExecutors,
        private val recipeDao: RecipeDao
) : RecipesDataSource {

    override fun saveRecipe(recipe: Recipe) {
        executors.discIO().execute { recipeDao.insertRecipe(recipe) }
    }

    override fun updateRecipe(recipe: Recipe) {
        executors.discIO().execute { recipeDao.updateRecipe(recipe) }
    }

    override fun deleteRecipe(recipe: Recipe) {
        executors.discIO().execute { recipeDao.deleteRecipe(recipe) }
    }

    override fun deleteSelectedRecipes(recipes: List<Recipe>) {
        executors.discIO().execute { recipeDao.deleteSelectedRecipes(recipes) }
    }

    override fun deleteAllRecipes() {
        executors.discIO().execute { recipeDao.deleteAllRecipes() }
    }

    override fun getRecipes(callback: RecipesDataSource.LoadRecipesCallback) {
        executors.discIO().execute {
            val recipes = recipeDao.getRecipes()
            executors.mainThread().execute {
                if (recipes.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onRecipesLoaded(recipes)
                }
            }
        }
    }

    override fun getRecipe(recipeId: Long, callback: RecipesDataSource.GetRecipeCallback) {
        executors.discIO().execute {
            val recipe = recipeDao.getRecipe(recipeId)
            executors.mainThread().execute {
                if (recipe == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onRecipeLoaded(recipe)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: RecipesRepository? = null

        fun getInstance(executors: AppExecutors, recipeDao: RecipeDao) =
                instance ?: synchronized(this) {
                    instance ?: RecipesRepository(executors, recipeDao).also { instance = it }
                }
    }

}