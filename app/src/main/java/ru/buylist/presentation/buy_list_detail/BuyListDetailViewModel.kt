package ru.buylist.presentation.buy_list_detail

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Category
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

class BuyListDetailViewModel(private val repository: BuyListsDataSource) : ViewModel() {

    private val _buyListId = MutableLiveData<Long>()
    private val _productToEdit = MutableLiveData<Int>()

    private val _triggers = MediatorLiveData<Pair<Long?, Int?>>()
            .apply {
                addSource(_buyListId) { value = Pair(it, _productToEdit.value) }
                addSource(_productToEdit) { value = Pair(_buyListId.value, it) }
            }

    private val _products = _triggers.switchMap { triggers ->
        repository.observeBuyList(triggers.first).map { computeResult(it, triggers.second) }
    }
    val products: LiveData<List<ItemWrapper>?> = _products

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_products) {
        it == null || it.isEmpty()
    }

    // Fields for two-way databinding
    val productName = MutableLiveData<String>()
    val productQuantity = MutableLiveData<String>()
    val productUnit = MutableLiveData<String>()

    // Flags to show and hide buttons
    val fabIsShown = MutableLiveData<Boolean>(true)
    val prevArrowIsShown = MutableLiveData<Boolean>(true)
    val nextArrowIsShown = MutableLiveData<Boolean>(true)

    private val items = mutableListOf<Item>()


    private var colorPosition = -1


    val wrapperCircles = MutableLiveData<List<CircleWrapper>>()
            .apply { value = emptyList() }
    private val circles = mutableListOf<String>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _addProductEvent = MutableLiveData<Event<Unit>>()
    val addProductEvent: LiveData<Event<Unit>> = _addProductEvent

    private val _newProductEvent = MutableLiveData<Event<Unit>>()
    val newProductEvent: LiveData<Event<Unit>> = _newProductEvent

    private val _saveProductEvent = MediatorLiveData<Event<Unit>>()
    val saveProductEvent: LiveData<Event<Unit>> = _saveProductEvent

    private val _productsAddedEvent = MutableLiveData<Event<Unit>>()
    val productAddedEvent: LiveData<Event<Unit>> = _productsAddedEvent


    fun start(buyListId: Long) {
        _buyListId.value = buyListId
    }

    fun addProduct() {
        _addProductEvent.value = Event(Unit)
    }

    fun addNewProduct() {
        _newProductEvent.value = Event(Unit)
    }

    fun hideNewProductLayout() {
        _productsAddedEvent.value = Event(Unit)
    }

    fun saveNewItem() {
        val name = productName.value
        if (name == null) {
            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        val newProduct = Item(name = name, category = getCategory())

        val quantity = productQuantity.value
        if (quantity != null) {
            newProduct.quantity = quantity
        }

        val unit = productUnit.value
        if (unit != null && quantity != null) {
            newProduct.unit = unit
        }

        createProduct(newProduct)
        productName.value = null
        productQuantity.value = null
        productUnit.value = null
        _saveProductEvent.value = Event(Unit)
    }

    fun edit(wrapper: ItemWrapper) {
        _productToEdit.value = wrapper.position
    }

    fun saveEditedData(wrapper: ItemWrapper, newName: String, newQuantity: String, newUnit: String) {
        if (newName.isEmpty()) {
            showSnackbarMessage(R.string.snackbar_empty_product_name)
            return
        }

        viewModelScope.launch {
            items[wrapper.position].apply {
                name = newName
                quantity = newQuantity
                unit = newUnit
            }
            repository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(items))
        }
        _productToEdit.value = null
    }

    fun delete(wrapper: ItemWrapper) = viewModelScope.launch {
        items.remove(wrapper.item)
        repository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(items))
    }


    fun changePurchaseStatus(position: Int) = viewModelScope.launch {
        items[position].isPurchased = !items[position].isPurchased
        items.sortWith(compareBy({ it.isPurchased }, { it.category.color }, { it.id }))
        repository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(items))
    }

    fun getCurrentColorPosition() = colorPosition

    fun saveCurrentColorPosition(position: Int) {
        colorPosition = position
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        val circleList: MutableList<CircleWrapper> = wrapperCircles.value as MutableList<CircleWrapper>
        checkSelectedCircles(circleList)
        selectedCircle.isSelected = !selectedCircle.isSelected
    }

    fun setupCircles(newCircles: List<String>) {
        circles.clear()
        circles.addAll(newCircles)
        wrapperCircles.value = getWrappedCircles(newCircles)
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.value = prev
        nextArrowIsShown.value = next
    }

    fun showHideFab(dy: Int) {
        fabIsShown.value = (dy <= 0)
    }

    private fun createProduct(item: Item) = viewModelScope.launch {
        items.add(item)
        items.sortWith(compareBy({ it.isPurchased }, { it.category.color }, { it.id }))
        repository.updateProducts(_buyListId.value, JsonUtils.convertItemsToJson(items))
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun checkSelectedCircles(list: MutableList<CircleWrapper>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val circle = iterator.next()
            if (circle.isSelected) {
                circle.isSelected = !circle.isSelected
                break
            }
        }
    }

    private fun getWrappedCircles(list: List<String>): List<CircleWrapper> {
        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrappedItems(list: List<Item>, position: Int?): List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val wrappedItem = ItemWrapper(item.copy(), i)
            if (position != null && position == i) {
                wrappedItem.isEditable = true
            }
            newList.add(wrappedItem)
        }
        return newList
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun computeResult(productsResult: Result<String>, position: Int?): List<ItemWrapper>? {
        return if (productsResult is Success) {
            items.clear()
            items.addAll(JsonUtils.convertItemsFromJson(productsResult.data))
            getWrappedItems(items, position)
        } else {
            showSnackbarMessage(R.string.snackbar_buy_lists_loading_error)
            null
        }
    }

}