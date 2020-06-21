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
    var wrapperPurchasedItems = MutableLiveData<List<ItemWrapper>>().apply { value = emptyList() }
    var items = mutableListOf<Item>()

    var wrapperCircles = MutableLiveData<List<CircleWrapper>>().apply { value = emptyList() }
    var circles = mutableListOf<String>()

    init {
        loadList()
    }

    fun saveNewItem() {
        val item = Item(name = itemName.get().toString(), category = getCategory())
        items.add(item)
        items.sortBy { it.category.color }
        updateUi()
        itemName.set("")
        buyList.items = JsonUtils.convertItemsToJson(items)
        buyListRepository.updateBuyList(buyList)
    }

    fun saveEditedData(wrapper: ItemWrapper, newName: String) {
        val list = getMutableWrapper(wrapper.item.isPurchased)
        wrapper.item.name = newName
        updateItemsWrapper(list, wrapper.item, wrapper.item.isPurchased, wrapper.globalPosition,
                wrapper.localPosition, false)
        buyList.items = JsonUtils.convertItemsToJson(items)
        buyListRepository.updateBuyList(buyList)
        isEditable = false
    }

    fun edit(wrapper: ItemWrapper) {
        val items = getMutableWrapper(false)
        val purchasedItems = getMutableWrapper(true)
        checkEditableField(items)
        checkEditableField(purchasedItems)
        val list = if (wrapper.item.isPurchased) purchasedItems else items
        updateItemsWrapper(list, wrapper.item, wrapper.item.isPurchased,
                wrapper.globalPosition, wrapper.localPosition, true)
        isEditable = true
    }

    fun delete(wrapper: ItemWrapper) {
        items.remove(wrapper.item)
        buyList.items = JsonUtils.convertItemsToJson(items)
        buyListRepository.updateBuyList(buyList)
        updateUi()
    }

    fun onItemClick(wrapper: ItemWrapper) {
        items[wrapper.globalPosition].isPurchased = !items[wrapper.globalPosition].isPurchased
        buyList.items = JsonUtils.convertItemsToJson(items)
        buyListRepository.updateBuyList(buyList)
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
        wrapperCircles.value = getWrapperCircles(newCircles)
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun updateUi() {
        val itemsToBuy = getWrapperItems(items, false)
        val purchasedItems = getWrapperItems(items, true)
        listIsEmpty.set(items.isEmpty())
        wrapperItems.value = itemsToBuy
        wrapperPurchasedItems.value = purchasedItems
    }

    private fun updateItemsWrapper(list: MutableList<ItemWrapper>, item: Item, isPurchased: Boolean,
                                   globalPosition: Int, localPosition: Int, isEditable: Boolean = false,
                                   isSelected: Boolean = false) {
        list.removeAt(localPosition)
        list.add(localPosition, ItemWrapper(item, globalPosition, localPosition, isEditable, isSelected))
        if (isPurchased) {
            wrapperPurchasedItems.value = list
        } else {
            wrapperItems.value = list
        }
    }

    private fun checkEditableField(list: MutableList<ItemWrapper>) {
        if (isEditable) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isEditable) {
                    updateItemsWrapper(list, item.item, item.item.isPurchased, item.globalPosition,
                            item.localPosition)
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

    private fun getMutableWrapper(isPurchased: Boolean): MutableList<ItemWrapper> {
        val list = mutableListOf<ItemWrapper>()
        if (isPurchased) {
            wrapperPurchasedItems.value?.let { list.addAll(it) }
        } else {
            wrapperItems.value?.let { list.addAll(it) }
        }
        return list
    }

    private fun getWrapperCircles(list: List<String>): List<CircleWrapper> {
        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrapperItems(list: List<Item>, isPurchased: Boolean): List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        var count = 0
        for ((i, item) in list.withIndex()) {
            if (item.isPurchased == isPurchased) {
                val itemWrapper = ItemWrapper(item, i, count)
                newList.add(itemWrapper)
                count++
            }
        }
        return newList
    }

    private fun loadList() {
        buyListRepository.getBuyList(buyListId, object : BuyListsDataSource.GetBuyListCallback {
            override fun onBuyListLoaded(buyList: BuyList) {
                items.clear()
                items.addAll(JsonUtils.convertItemsFromJson(buyList.items))
                wrapperItems.value = getWrapperItems(items, false)
                wrapperPurchasedItems.value = getWrapperItems(items, true)
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