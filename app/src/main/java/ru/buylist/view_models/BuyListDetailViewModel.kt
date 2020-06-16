package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.BuyListWrapper
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.ItemWrapper
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

    var wrapperList = MutableLiveData<List<ItemWrapper>>().apply { value = emptyList() }
    var items = mutableListOf<Item>()

    init {
        loadList()
    }

    fun edit(itemWrapper: ItemWrapper) {

    }

    fun delete(itemWrapper: ItemWrapper) {

    }

    fun saveEditedData(itemWrapper: ItemWrapper, name: String) {

    }

    private fun getWrapperList(list: List<Item>): List<ItemWrapper> {
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
                wrapperList.value = getWrapperList(items)
                listIsEmpty.set(items.isEmpty())
            }

            override fun onDataNotAvailable() {
                wrapperList.value = emptyList()
                listIsEmpty.set(true)
            }

        })
    }


}