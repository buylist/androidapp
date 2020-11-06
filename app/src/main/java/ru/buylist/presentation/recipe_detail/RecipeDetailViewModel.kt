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
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

/**
 * ViewModel for the recipe detail screen.
 */

class RecipeDetailViewModel(
        private val repository: RecipesDataSource,
        private val recipeId: Long) : ViewModel() {

    val fabIsShown = MutableLiveData<Boolean>(true)

    val wrappedItems = MutableLiveData<List<ItemWrapper>>()
            .apply { value = emptyList() }
    val wrappedSteps = MutableLiveData<List<CookingStepWrapper>>()
            .apply { value = emptyList() }

    private lateinit var _recipe: Recipe

    val recipe = MutableLiveData<Recipe>()

    private val _editEvent = MutableLiveData<Event<Recipe>>()
    val editEvent: LiveData<Event<Recipe>> = _editEvent

    init {
        loadRecipe()
    }

    fun editRecipe() {
        _editEvent.value = Event(_recipe)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = dy <= 0
    }

    private fun getWrappedSteps(list: List<CookingStep>): List<CookingStepWrapper> {
        val newList = mutableListOf<CookingStepWrapper>()
        for ((i, step) in list.withIndex()) {
            val wrappedStep = CookingStepWrapper(step.copy(), i)
            newList.add(wrappedStep)
        }
        return newList
    }

    private fun getWrappedItems(list: List<Item>): List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val wrappedItem = ItemWrapper(item.copy(), i)
            newList.add(wrappedItem)
        }
        return newList
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            repository.getRecipe(recipeId).let { result ->
                if (result is Success) {
                    _recipe = result.data
                    recipe.value = result.data
                    wrappedItems.value = getWrappedItems(JsonUtils
                            .convertItemsFromJson(result.data.items))
                    wrappedSteps.value = getWrappedSteps(JsonUtils
                            .convertCookingStepsFromJson(result.data.cookingSteps))
                } else {
                    TODO("Error while loading recipe $result")
                }
            }
        }
    }
}