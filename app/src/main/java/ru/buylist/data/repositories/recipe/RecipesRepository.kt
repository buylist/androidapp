package ru.buylist.data.repositories.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.Error
import ru.buylist.data.Result.Success
import ru.buylist.data.dao.GlobalItemDao
import ru.buylist.data.dao.RecipeDao
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.entity.Recipe

class RecipesRepository private constructor(
        private val recipeDao: RecipeDao,
        private val globalItemDao: GlobalItemDao,
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

    override suspend fun getRecipe(recipeId: Long): Result<Recipe> = withContext(ioDispatcher) {
        try {
            val recipe = recipeDao.getRecipe(recipeId)
            if (recipe == null) {
                return@withContext Error(Exception("Рецепт не найден"))
            } else {
                return@withContext Success(recipe)
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override fun observeRecipe(recipeId: Long): LiveData<Result<Recipe>> {
        return recipeDao.observeRecipeById(recipeId).map {
            Success(it)
        }
    }

    override suspend fun getTags(): Result<List<GlobalItem>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(globalItemDao.getGlobalItems())
        } catch (e: Exception) {
            Error(e)
        }
    }

    companion object {
        @Volatile
        private var instance: RecipesRepository? = null

        fun getInstance(recipeDao: RecipeDao, globalItemDao: GlobalItemDao) =
                instance ?: synchronized(this) {
                    instance ?: RecipesRepository(recipeDao, globalItemDao).also { instance = it }
                }
    }

}