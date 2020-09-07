package ru.buylist.data.repositories.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.*
import ru.buylist.data.dao.RecipeDao
import ru.buylist.data.entity.Recipe
import ru.buylist.utils.AppExecutors
import java.lang.Exception

class RecipesRepository private constructor(
        private val executors: AppExecutors,
        private val recipeDao: RecipeDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RecipesDataSource {

    override suspend fun saveRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        recipeDao.insertRecipe(recipe)
    }

    override suspend fun updateRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        recipeDao.updateRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) = withContext(ioDispatcher) {
        recipeDao.deleteRecipe(recipe)
    }

    override suspend fun deleteSelectedRecipes(recipes: List<Recipe>) = withContext(ioDispatcher) {
        recipeDao.deleteSelectedRecipes(recipes)
    }

    override suspend fun deleteAllRecipes() = withContext(ioDispatcher) {
        recipeDao.deleteAllRecipes()
    }

    override fun observeRecipes(): LiveData<Result<List<Recipe>>> {
        return recipeDao.observeRecipes().map {
            Success(it)
        }
    }

    override suspend fun getRecipes(): Result<List<Recipe>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(recipeDao.getRecipes())
        } catch (e: Exception) {
            Error(e)
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