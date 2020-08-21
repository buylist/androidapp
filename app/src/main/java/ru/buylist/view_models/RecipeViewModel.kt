package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.RecipeWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource.LoadRecipesCallback

class RecipeViewModel(private val repository: RecipesDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var fabIsShown = ObservableBoolean(true)
    var isEditable: Boolean = false
    var recipeTitle = ObservableField("")

    val wrappedRecipes = MutableLiveData<List<RecipeWrapper>>()
            .apply { value = emptyList() }
    var recipes = mutableListOf<Recipe>()

    init {
        loadList()
    }

    fun save() {
        val newRecipe = Recipe()
        val title = recipeTitle.get().toString().trim()
        if (title.isNotEmpty()) {
            newRecipe.title = title
        }
        repository.saveRecipe(newRecipe)
        recipes.add(newRecipe)
        updateUi()
        recipeTitle.set("")
    }

    fun saveEditedData(wrapper: RecipeWrapper, newTitle: String) {
        if (newTitle.trim().isEmpty()) return

        val list = extractDataFromWrappedRecipes()
        wrapper.recipe.title = newTitle.trim()
        recipes[wrapper.position].title = newTitle.trim()

        updateWrappedRecipes(list, wrapper)
        repository.updateRecipe(wrapper.recipe)
        isEditable = false
    }

    fun edit(wrapper: RecipeWrapper) {
        val list = extractDataFromWrappedRecipes()
        updateWrappedRecipes(list, wrapper, true)
        isEditable = true
    }

    fun delete(wrapper: RecipeWrapper) {
        recipes.remove(wrapper.recipe)
        repository.deleteRecipe(wrapper.recipe)
        updateUi()
    }

    fun showHideFab(dy: Int) {
        fabIsShown.set(dy <= 0)
    }

    private fun updateUi() {
        wrappedRecipes.value = getWrappedRecipes(recipes)
        listIsEmpty.set(recipes.isEmpty())
    }

    private fun updateWrappedRecipes(list: MutableList<RecipeWrapper>, wrapper: RecipeWrapper,
                                      isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = wrapper.copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(wrapper.position)
        list.add(wrapper.position, newWrapper)
        wrappedRecipes.value = list
    }

    private fun extractDataFromWrappedRecipes(): MutableList<RecipeWrapper> {
        val list = mutableListOf<RecipeWrapper>()
        wrappedRecipes.value?.let { list.addAll(it) }
        return list
    }

    private fun getWrappedRecipes(recipes: List<Recipe>): List<RecipeWrapper> {
        val newList = mutableListOf<RecipeWrapper>()
        for ((i, recipe) in recipes.withIndex()) {
            val wrappedRecipe = RecipeWrapper(recipe.copy(), i)
            newList.add(wrappedRecipe)
        }
        return newList
    }

    private fun loadList() {
        repository.getRecipes(object : LoadRecipesCallback {
            override fun onRecipesLoaded(loadedRecipes: List<Recipe>) {
                wrappedRecipes.value = getWrappedRecipes(loadedRecipes)
                recipes.clear()
                recipes.addAll(loadedRecipes)
                listIsEmpty.set(recipes.isEmpty())
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }
}