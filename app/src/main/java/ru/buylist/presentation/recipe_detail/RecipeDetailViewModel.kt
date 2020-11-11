package ru.buylist.presentation.recipe_detail

import androidx.lifecycle.*
import ru.buylist.data.Result
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

class RecipeDetailViewModel(private val repository: RecipesDataSource) : ViewModel() {

    private val _recipeId = MutableLiveData<Long>()

    private val _recipe = _recipeId.switchMap { recipeId ->
        repository.observeRecipe(recipeId).map { computeResult(it) }
    }
    val recipe: LiveData<Recipe?> = _recipe

    private val _ingredients = MutableLiveData<List<Item>>()
    val ingredients: LiveData<List<Item>> = _ingredients

    private val _cookingStep = MutableLiveData<List<CookingStep>>()
    val cookingStep: LiveData<List<CookingStep>> = _cookingStep

    val isDataAvailable = _recipe.map { it != null }

    private val _editEvent = MutableLiveData<Event<Unit>>()
    val editEvent: LiveData<Event<Unit>> = _editEvent

    val fabIsShown = MutableLiveData<Boolean>(true)


    fun start(recipeId: Long) {
        _recipeId.value = recipeId
    }

    fun editRecipe() {
        _editEvent.value = Event(Unit)
    }

    fun showHideFab(dy: Int) {
        if (isDataAvailable.value == false) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = dy <= 0
    }

    private fun computeResult(recipeResult: Result<Recipe>): Recipe? {
        if (recipeResult is Success) {
            _ingredients.value = JsonUtils.convertItemsFromJson(recipeResult.data.items)
            _cookingStep.value = JsonUtils
                    .convertCookingStepsFromJson(recipeResult.data.cookingSteps)
            return recipeResult.data
        } else {
            TODO("Error while loading recipe $recipeResult")
        }
    }
}