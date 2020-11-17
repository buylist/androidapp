package ru.buylist.presentation.move_products_from_pattern

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Pattern
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

class MoveProductsFromPatternViewModel(
        private val buyListRepository: BuyListsDataSource,
        private val patternsRepository: PatternsDataSource
) : ViewModel() {

    private val _buyListId = MutableLiveData<Long>()

    private val _patterns: LiveData<List<Pattern>> = _buyListId.switchMap {
        patternsRepository.observePatterns().distinctUntilChanged().switchMap { loadPatterns(it) }
    }

    val patterns: LiveData<List<Pattern>> = _patterns

    private val _buyListProducts = mutableListOf<Item>()

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_patterns) {
        it.isEmpty() || it == null
    }

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText


    fun start(buyListId: Long) {
        _buyListId.value = buyListId
        loadProductsFromBuyList(buyListId)
    }

    fun moveProducts(pattern: Pattern) {
        if (pattern.items.isEmpty()) {
            showSnackbarMessage(R.string.snackbar_no_products_in_pattern)
            return
        }
        updateProducts(pattern)
    }

    private fun updateProducts(pattern: Pattern) = viewModelScope.launch {
        val newItems = mutableListOf<Item>().apply {
            addAll(JsonUtils.convertItemsFromJson(pattern.items))
            addAll(_buyListProducts)
        }

        newItems.sortWith(compareBy({ it.isPurchased }, { it.color }, { it.id }))
        buyListRepository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(newItems))
        showSnackbarMessage(R.string.snackbar_products_moved_from_pattern)
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun onProductsLoaded(result: String) {
        _buyListProducts.addAll(JsonUtils.convertItemsFromJson(result))
    }

    private fun loadProductsFromBuyList(buyListId: Long) = viewModelScope.launch {
        val result = buyListRepository.getProducts(buyListId)
        if (result is Success) {
            onProductsLoaded(result.data)
        }
    }

    private fun loadPatterns(patternsResult: Result<List<Pattern>>): LiveData<List<Pattern>> {
        val result = MutableLiveData<List<Pattern>>()

        if (patternsResult is Success) {
            result.value = patternsResult.data
        } else {
            result.value = emptyList()
        }
        return result
    }

}

