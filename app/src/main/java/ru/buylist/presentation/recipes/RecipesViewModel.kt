package ru.buylist.presentation.recipes

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.RecipeWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.R

class RecipesViewModel(private val repository: RecipesDataSource) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)

    private val _recipes: LiveData<List<RecipeWrapper>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            TODO("Load recipes from remote data source.")
        }
        repository.observeRecipes().distinctUntilChanged().switchMap { loadRecipes(it) }
    }

    val recipes: LiveData<List<RecipeWrapper>> = _recipes

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_recipes) {
        it.isEmpty()
    }

    val fabIsShown = MutableLiveData<Boolean>(true)

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _newRecipeEvent = MutableLiveData<Event<Recipe>>()
    val newRecipeEvent: LiveData<Event<Recipe>> = _newRecipeEvent

    private val _detailsEvent = MutableLiveData<Event<Recipe>>()
    val detailsEvent: LiveData<Event<Recipe>> = _detailsEvent



    fun addNewRecipe() {
        _newRecipeEvent.value = Event(Recipe(0L))
    }

    fun edit(wrapper: RecipeWrapper) {
        _newRecipeEvent.value = Event(wrapper.recipe)
    }

    fun delete(wrapper: RecipeWrapper) = viewModelScope.launch {
        repository.deleteRecipe(wrapper.recipe)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = (dy <= 0)
    }

    fun showDetail(recipe: Recipe) {
        _detailsEvent.value = Event(recipe)
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun getWrappedRecipes(recipes: List<Recipe>): List<RecipeWrapper> {
        val newList = mutableListOf<RecipeWrapper>()
        for ((i, recipe) in recipes.withIndex()) {
            val wrappedRecipe = RecipeWrapper(recipe.copy(), i)
            newList.add(wrappedRecipe)
        }
        return newList
    }

    private fun loadRecipes(recipesResult: Result<List<Recipe>>): LiveData<List<RecipeWrapper>> {
        val result = MutableLiveData<List<RecipeWrapper>>()

        if (recipesResult is Success) {
            viewModelScope.launch {
                result.value = getWrappedRecipes(recipesResult.data)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.snackbar_recipes_loading_error)
        }
        return result
    }
}