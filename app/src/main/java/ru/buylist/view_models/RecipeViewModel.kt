package ru.buylist.view_models

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.Recipe
import ru.buylist.data.repositories.recipe.RecipesDataSource

class RecipeViewModel(private val repository: RecipesDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var recipeTitle = ObservableField("")

    var list = ObservableArrayList<Recipe>()

    init {
        loadList()
    }

    fun save(id: Long = 0) {
        val recipe: Recipe = if (id == 0L) {
            Recipe(title = recipeTitle.get().toString())
        } else {
            Recipe(id = id)
        }
        repository.saveRecipe(recipe)
        recipeTitle.set("")
        loadList()
    }

    fun edit(recipe: Recipe) {

    }

    fun delete(recipe: Recipe) {

    }

    private fun loadList() {
        repository.getRecipes(object : RecipesDataSource.LoadRecipesCallback {
            override fun onRecipesLoaded(recipes: List<Recipe>) {
                list.clear()
                list.addAll(recipes)
                listIsEmpty.set(false)
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }
}