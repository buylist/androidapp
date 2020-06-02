package ru.buylist.view_models

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.BuyList
import ru.buylist.data.repositories.buyList.BuyListsDataSource

class BuyListViewModel(private val repository: BuyListsDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var buyListTitle = ObservableField("")

    var list = ObservableArrayList<BuyList>()

    init {
        loadList()
    }

    fun save(id: Long = 0) {
        val buyList: BuyList = if (id == 0L) {
            BuyList(title = buyListTitle.get().toString())
        } else {
            BuyList(id = id)
        }
        repository.saveBuyList(buyList)
        buyListTitle.set("")
        loadList()
    }

    fun edit(buyList: BuyList) {

    }

    fun delete(buyList: BuyList) {

    }

    private fun loadList() {
        repository.getBuyLists(object : BuyListsDataSource.LoadBuyListsCallback {
            override fun onBuyListsLoaded(buyLists: List<BuyList>) {
                list.clear()
                list.addAll(buyLists)
                listIsEmpty.set(false)
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }
}