package ru.buylist.presentation.recipe_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

/**
 * ViewModel for the recipe detail screen.
 */

class RecipeDetailViewModel(
        private val repository: RecipesDataSource,
        private val recipeId: Long) : ViewModel() {

    private val _recipe = MutableLiveData<Recipe>()
    val recipe: LiveData<Recipe> = _recipe

    private val _ingredients = MutableLiveData<List<Item>>()
    val ingredients: LiveData<List<Item>> = _ingredients

    private val _cookingStep = MutableLiveData<List<CookingStep>>()
    val cookingStep: LiveData<List<CookingStep>> = _cookingStep

    private val _editEvent = MutableLiveData<Event<Unit>>()
    val editEvent: LiveData<Event<Unit>> = _editEvent

    val fabIsShown = MutableLiveData<Boolean>(true)

    init {
        loadRecipe()
    }

    fun editRecipe() {
        _editEvent.value = Event(Unit)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = dy <= 0
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            repository.getRecipe(recipeId).let { result ->
                if (result is Success) {
                    _recipe.value = result.data
                    _ingredients.value = JsonUtils.convertItemsFromJson(result.data.items)
                    _cookingStep.value = JsonUtils
                            .convertCookingStepsFromJson(result.data.cookingSteps)
                } else {
                    TODO("Error while loading recipe $result")
                }
            }
        }
    }
}