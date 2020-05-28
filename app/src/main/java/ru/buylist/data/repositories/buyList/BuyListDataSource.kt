package ru.buylist.data.repositories.buyList

import ru.buylist.data.entity.BuyList

interface BuyListDataSource {

    interface GetBuyListCallback {
        fun onBuyListsLoaded(buyList: BuyList)
        fun onDataNotAvailable()
    }

    interface LoadBuyListsCallback {
        fun onBuyListsLoaded(buyLists: List<BuyList>)
        fun onDataNotAvailable()
    }

    fun saveBuyList(buyList: BuyList)

    fun updateBuyList(buyList: BuyList)

    fun deleteBuyList(buyList: BuyList)

    fun deleteSelectedBuyList(buyLists: List<BuyList>)

    fun deleteAllBuyList()

    fun getBuyLists(callback: LoadBuyListsCallback)

    fun getBuyList(buyListId: Long, callback: GetBuyListCallback)
}