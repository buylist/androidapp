package ru.buylist.presentation.product_dictionary

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.utils.CategoryInfo
import ru.buylist.utils.Event

class ProductDictionaryViewModel(private val repository: GlobalItemsDataSource) : ViewModel() {

    private val _products: LiveData<List<GlobalItem>> = repository.observeGlobalItems().map {
        loadProducts(it)
    }
    val products: LiveData<List<GlobalItem>> = _products

    private val _colors = MutableLiveData<List<String>>()
    private val _selectedColor = MutableLiveData<Int>()

    private val _circlesUpdate
            = MediatorLiveData<Pair<List<String>?, Int?>>().apply {
        addSource(_colors) { value = Pair(it, _selectedColor.value) }
        addSource(_selectedColor) { value = Pair(_colors.value, it) }
    }

    private val _circles: LiveData<List<CircleWrapper>> = _circlesUpdate.map { pair ->
        getWrappedCircles(pair.first, pair.second)
    }
    val circles: LiveData<List<CircleWrapper>> = _circles

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_products) {
        it == null || it.isEmpty()
    }

    val productName = MutableLiveData<String>()

    val fabIsShown = MutableLiveData<Boolean>(true)
    val prevArrowIsShown = MutableLiveData<Boolean>(true)
    val nextArrowIsShown = MutableLiveData<Boolean>(true)

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _addProductEvent = MutableLiveData<Event<Unit>>()
    val addProductEvent: LiveData<Event<Unit>> = _addProductEvent

    private val _productCreated = MutableLiveData<Event<Unit>>()
    val productCreated: LiveData<Event<Unit>> = _productCreated


    fun start(colors: List<String>) {
        _colors.value = colors
    }

    fun addProduct() {
        _addProductEvent.value = Event(Unit)
    }

    fun saveNewProduct() {
        val name = productName.value
        if (name == null) {
            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        createProduct(GlobalItem(name = name, color = getColor()))
        productName.value = null
    }

    fun hideNewProductLayout() {
        _productCreated.value = Event(Unit)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = (dy <= 0)
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.value = prev
        nextArrowIsShown.value = next
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        _selectedColor.value = selectedCircle.position
    }

    private fun createProduct(product: GlobalItem) = viewModelScope.launch {
        repository.saveGlobalItem(product)
    }

    private fun getColor(): String {
        _selectedColor.value?.let {position ->
            _colors.value?.let { colors ->
                return colors[position]
            }
        } ?: return CategoryInfo.COLOR
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun getWrappedCircles(colors: List<String>?, position: Int?): List<CircleWrapper> {
        if (colors == null) return emptyList()

        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in colors.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            if (position != null && position == i) {
                circleWrapper.isSelected = true
            }
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun loadProducts(productsResult: Result<List<GlobalItem>>): List<GlobalItem> {
        return if (productsResult is Success) {
            productsResult.data
        } else {
            emptyList()
        }
    }

}