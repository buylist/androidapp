package ru.buylist.data.repositories.items

import ru.buylist.data.entity.GlobalItem

interface GlobalItemDataSource {

    interface GetGlobalItemCallback {
        fun onGlobalItemLoaded(globalItem: GlobalItem)
        fun onDataNotAvailable()
    }

    interface LoadGlobalItemsCallback {
        fun onGlobalItemsLoaded(globalItems: List<GlobalItem>)
        fun onDataNotAvailable()
    }

    fun saveGlobalItem(globalItem: GlobalItem)

    fun updateGlobalItem(globalItem: GlobalItem)

    fun updateGlobalItemCategory(category: String)

    fun updateGlobalItemColor(color: String)

    fun getGlobalItems(callback: LoadGlobalItemsCallback)

    fun getGlobalItem(globalItemId: Long, callback: GetGlobalItemCallback)
}