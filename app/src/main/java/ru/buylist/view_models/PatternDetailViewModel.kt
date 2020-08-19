package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.*
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource.*
import ru.buylist.utils.JsonUtils

class PatternDetailViewModel(
        private val patternsRepository: PatternsDataSource,
        private val globalItemsRepository: GlobalItemsDataSource,
        private val patternId: Long) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var fabIsShown = ObservableBoolean(true)
    var prevArrowIsShown = ObservableBoolean(true)
    var nextArrowIsShown = ObservableBoolean(true)
    var itemName = ObservableField("")

    var isEditable: Boolean = false
    private var colorPosition = -1
    private lateinit var pattern: Pattern
    val items = mutableListOf<Item>()
    val wrappedItems = MutableLiveData<List<ItemWrapper>>().apply { value = emptyList() }
    val wrapperCircles = MutableLiveData<List<CircleWrapper>>().apply { value = emptyList() }
    private val circles = mutableListOf<String>()

    init {
        loadList()
    }

    fun saveNewItem() {
        val item = Item(name = itemName.get().toString(), category = getCategory())
        items.add(item)
        items.sortWith(compareBy({ it.isPurchased }, { it.category.color }, { it.id }))
        updateUi()
        itemName.set("")
        pattern.items = JsonUtils.convertItemsToJson(items)
        patternsRepository.updatePattern(pattern)
    }

    fun saveEditedData(wrapper: ItemWrapper, newName: String) {
        val list = extractDataFromWrappedItems()
        wrapper.item.name = newName
        items[wrapper.position].name = newName

        updateWrappedItems(list, wrapper, false)
        pattern.items = JsonUtils.convertItemsToJson(items)
        patternsRepository.updatePattern(pattern)
        isEditable = false
    }

    fun edit(wrapper: ItemWrapper) {
        val items = extractDataFromWrappedItems()
        checkEditableField(items)
        updateWrappedItems(items, wrapper, true)
        isEditable = true
    }

    fun delete(wrapper: ItemWrapper) {
        // TODO: добавить возможность отмены удаления

        items.remove(wrapper.item)
        pattern.items = JsonUtils.convertItemsToJson(items)
        patternsRepository.updatePattern(pattern)
        updateUi()
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
        prevArrowIsShown.set(prev)
        nextArrowIsShown.set(next)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.set(dy <= 0)
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun updateUi() {
        wrappedItems.value = getWrappedItems(items)
        listIsEmpty.set(items.isEmpty())
    }

    private fun updateWrappedItems(list: MutableList<ItemWrapper>, wrapper: ItemWrapper,
                                   isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = wrapper.copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(wrapper.position)
        list.add(wrapper.position, newWrapper)
        wrappedItems.value = list
    }

    private fun checkEditableField(list: MutableList<ItemWrapper>) {
        if (isEditable) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isEditable) {
                    updateWrappedItems(list, item)
                    break
                }
            }
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

    private fun extractDataFromWrappedItems(): MutableList<ItemWrapper> {
        val list = mutableListOf<ItemWrapper>()
        wrappedItems.value?.let { list.addAll(it) }
        return list
    }

    private fun getWrappedCircles(list: List<String>): List<CircleWrapper> {
        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrappedItems(list: List<Item>) : List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val wrappedItem = ItemWrapper(item.copy(), i)
            newList.add(wrappedItem)
        }
        return newList
    }

    private fun loadList() {
        patternsRepository.getPattern(patternId, object : GetPatternCallback {
            override fun onPatternLoaded(pattern: Pattern) {
                items.clear()
                items.addAll(JsonUtils.convertItemsFromJson(pattern.items))
                wrappedItems.value = getWrappedItems(items)
                listIsEmpty.set(items.isEmpty())
                this@PatternDetailViewModel.pattern = pattern
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }


}