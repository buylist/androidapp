package ru.buylist.presentation.buy_lists

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.wrappers.BuyListWrapper
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.utils.Event

/**
 * ViewModel for the buy lists screen.
 */
class BuyListsViewModel(private val repository: BuyListsDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var fabIsShown = ObservableBoolean(true)
    var isEditable: Boolean = false
    var buyListTitle = ObservableField("")

    var wrapperList = MutableLiveData<List<BuyListWrapper>>().apply { value = emptyList() }
    var buyLists = mutableListOf<BuyList>()

    private val _addBuyListsEvent = MutableLiveData<Event<Unit>>()
    val addBuyList: LiveData<Event<Unit>> = _addBuyListsEvent

    private val _buyListCreated = MutableLiveData<Event<Unit>>()
    val buyListCreated: LiveData<Event<Unit>> = _buyListCreated

    private val _detailsEvent = MutableLiveData<Event<BuyList>>()
    val detailsEvent: LiveData<Event<BuyList>> = _detailsEvent

    init {
        loadList()
    }

    fun addNewBuyList() {
        _addBuyListsEvent.value = Event(Unit)
    }

    fun showDetail(buyList: BuyList) {
        _detailsEvent.value = Event(buyList)
    }

    fun save() {
        val buyList = BuyList(title = buyListTitle.get().toString())
        repository.saveBuyList(buyList)
        buyLists.add(buyList)
        updateUi()
        buyListTitle.set("")
        hideLayoutWithFields()
    }

    fun hideLayoutWithFields() {
        _buyListCreated.value = Event((Unit))
    }

    fun saveEditedData(buyListWrapper: BuyListWrapper, newTitle: String) {
        val items = getItems()
        buyListWrapper.buyList.title = newTitle
        updateBuyListWrapper(items, buyListWrapper.buyList, buyListWrapper.position, false)
        repository.updateBuyList(buyListWrapper.buyList)
        isEditable = false
    }

    fun edit(buyListWrapper: BuyListWrapper) {
        val items = getItems()
        checkEditableField(items)
        updateBuyListWrapper(items, buyListWrapper.buyList, buyListWrapper.position, true)
        isEditable = true
    }

    fun delete(buyListWrapper: BuyListWrapper) {
        buyLists.remove(buyListWrapper.buyList)
        repository.deleteBuyList(buyListWrapper.buyList)
        updateUi()
    }

    fun showHideFab(dy: Int) {
        if (isEditable) {
            fabIsShown.set(false)
            return
        }
        fabIsShown.set(dy <= 0)
    }

    private fun updateUi() {
        val items = getWrapperList(buyLists)
        listIsEmpty.set(items.isEmpty())
        wrapperList.value = items
    }

    private fun updateBuyListWrapper(items: MutableList<BuyListWrapper>, buyList: BuyList, position: Int,
                                     isEditable: Boolean = false, isSelected: Boolean = false) {
        items.removeAt(position)
        items.add(position, BuyListWrapper(buyList, position, isEditable, isSelected))
        wrapperList.value = items
    }

    private fun checkEditableField(items: MutableList<BuyListWrapper>) {
        if (isEditable) {
            val iterator = items.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isEditable) {
                    updateBuyListWrapper(items, item.buyList, item.position)
                    break
                }
            }
        }
    }

    private fun getItems(): MutableList<BuyListWrapper> {
        val items: MutableList<BuyListWrapper> = mutableListOf()
        wrapperList.value?.let { items.addAll(it) }
        return items
    }

    private fun getWrapperList(buyLists: List<BuyList>): List<BuyListWrapper> {
        val newList = mutableListOf<BuyListWrapper>()
        for ((i, buyList) in buyLists.withIndex()) {
            val buyListWrapper = BuyListWrapper(buyList, i)
            newList.add(buyListWrapper)
        }
        return newList
    }

    private fun loadList() {
        repository.getBuyLists(object : BuyListsDataSource.LoadBuyListsCallback {
            override fun onBuyListsLoaded(buyLists: List<BuyList>) {
                wrapperList.value = getWrapperList(buyLists).toMutableList()
                this@BuyListsViewModel.buyLists.clear()
                this@BuyListsViewModel.buyLists.addAll(buyLists)
                listIsEmpty.set(false)
            }

            override fun onDataNotAvailable() {
                wrapperList.value = emptyList()
                listIsEmpty.set(true)
            }
        })
    }
}