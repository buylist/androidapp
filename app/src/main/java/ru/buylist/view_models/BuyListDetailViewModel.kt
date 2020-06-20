package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.*
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.utils.JsonUtils

class BuyListDetailViewModel(
        private val buyListRepository: BuyListsDataSource,
        private val globalItemsRepository: GlobalItemsDataSource,
        private val buyListId: Long) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var fabIsShown = ObservableBoolean(true)
    var isEditable: Boolean = false
    var itemName = ObservableField("")
    private var colorPosition = -1
    private lateinit var buyList: BuyList

    var wrapperItems = MutableLiveData<List<ItemWrapper>>().apply { value = emptyList() }
    var items = mutableListOf<Item>()

    var wrapperCircles = MutableLiveData<List<CircleWrapper>>().apply { value = emptyList() }
    var circles = mutableListOf<String>()

    init {
        loadList()
    }

    fun saveNewItem() {
        val item  = Item(name = itemName.get().toString(), category = getCategory())
        items.add(item)
        items.sortBy { it.category.color }
        updateUi()
        itemName.set("")
        buyList.items = JsonUtils.convertItemsToJson(items)
        buyListRepository.updateBuyList(buyList)
    }

    fun edit(itemWrapper: ItemWrapper) {

    }

    fun delete(itemWrapper: ItemWrapper) {

    }

    fun saveEditedData(itemWrapper: ItemWrapper, name: String) {

    }

    fun getCurrentColorPosition() = colorPosition

    fun saveCurrentColorPosition(position: Int) {
        colorPosition = position
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        val circleList: MutableList<CircleWrapper> = wrapperCircles.value as MutableList<CircleWrapper>
        checkSelectedCircles(circleList)
        updateCirclesWrapper(circleList, selectedCircle.color, selectedCircle.position, !selectedCircle.isSelected)
    }

    fun setupCircles(newCircles: List<String>) {
        circles.clear()
        circles.addAll(newCircles)
        wrapperCircles.value = getWrapperCircles(newCircles)
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun updateUi() {
        val items = getWrapperItems(items)
        listIsEmpty.set(items.isEmpty())
        wrapperItems.value = items
    }

    private fun updateCirclesWrapper(list: MutableList<CircleWrapper>, color: String, position: Int, isSelected: Boolean = false) {
        list.removeAt(position)
        list.add(position, CircleWrapper(color, position, isSelected))
        wrapperCircles.value = list
    }

    private fun checkSelectedCircles(list: MutableList<CircleWrapper>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val circle = iterator.next()
            if (circle.isSelected) {
                updateCirclesWrapper(list, circle.color, circle.position, false)
                break
            }
        }
    }

    private fun getWrapperCircles(list: List<String>): List<CircleWrapper> {
        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrapperItems(list: List<Item>): List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val itemWrapper = ItemWrapper(item, i)
            newList.add(itemWrapper)
        }
        return newList
    }

    private fun loadList() {
        buyListRepository.getBuyList(buyListId, object : BuyListsDataSource.GetBuyListCallback {
            override fun onBuyListLoaded(buyList: BuyList) {
                items.clear()
                items.addAll(JsonUtils.convertItemsFromJson(buyList.items))
                wrapperItems.value = getWrapperItems(items)
                listIsEmpty.set(items.isEmpty())
                this@BuyListDetailViewModel.buyList = buyList
            }

            override fun onDataNotAvailable() {
                wrapperItems.value = emptyList()
                listIsEmpty.set(true)
            }

        })
    }


}