package ru.buylist.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource.*
import ru.buylist.utils.JsonUtils

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

    init {
        loadRecipe()
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
        repository.getRecipe(recipeId, object : GetRecipeCallback {
            override fun onRecipeLoaded(loadedRecipe: Recipe) {
                recipe.value = loadedRecipe
                wrappedItems.value = getWrappedItems(JsonUtils
                        .convertItemsFromJson(loadedRecipe.items))
                wrappedSteps.value = getWrappedSteps(JsonUtils
                        .convertCookingStepsFromJson(loadedRecipe.cookingSteps))
            }

            override fun onDataNotAvailable() {
                // TODO: show error "Error while loading recipe"
            }

        })
    }
}