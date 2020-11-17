package ru.buylist.presentation.move_products_from_recipe

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.*
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

class MoveFromRecipeViewModel(
        private val buyListRepository: BuyListsDataSource,
        private val recipesRepository: RecipesDataSource
) : ViewModel() {
    
    private val _buyListId = MutableLiveData<Long>()
    
    private val _recipes: LiveData<List<Recipe>> = _buyListId.switchMap { id ->
        recipesRepository.observeRecipes().distinctUntilChanged().switchMap { loadRecipes(it) }
    }

    val recipes: LiveData<List<Recipe>> = _recipes

    private val buyListProducts = mutableListOf<Item>()

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_recipes) {
        it.isEmpty() || it == null
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText



    fun start(buyListId: Long) {
        _buyListId.value = buyListId
        loadProductsFromBuyList(buyListId)
    }

    fun moveProducts(recipe: Recipe) {
        if (recipe.items.isEmpty()) {
            showSnackbarMessage(R.string.snackbar_no_products_in_recipe)
            return
        }
        updateProducts(recipe)
    }

    private fun updateProducts(recipe: Recipe) = viewModelScope.launch {
        val newItems = mutableListOf<Item>().apply {
            addAll(JsonUtils.convertItemsFromJson(recipe.items))
            addAll(buyListProducts)
        }

        newItems.sortWith(compareBy({ it.isPurchased }, { it.color }, { it.id }))
        buyListRepository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(newItems))
        showSnackbarMessage(R.string.snackbar_products_moved_from_recipe)
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun onProductsLoaded(result: String) {
        buyListProducts.addAll(JsonUtils.convertItemsFromJson(result))
    }

    private fun loadProductsFromBuyList(buyListId: Long) = viewModelScope.launch {
        val result = buyListRepository.getProducts(buyListId)
        if (result is Success) {
            onProductsLoaded(result.data)
        }
    }

    private fun loadRecipes(recipesResult: Result<List<Recipe>>): LiveData<List<Recipe>> {
        val result = MutableLiveData<List<Recipe>>()

        if (recipesResult is Success) {
            result.value = recipesResult.data
        } else {
            result.value = emptyList()
        }
        return result
    }
}