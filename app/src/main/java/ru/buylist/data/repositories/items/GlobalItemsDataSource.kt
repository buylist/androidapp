package ru.buylist.data.repositories.items

import androidx.lifecycle.LiveData
import ru.buylist.data.Result
import ru.buylist.data.entity.GlobalItem

interface GlobalItemsDataSource {

    suspend fun saveGlobalItem(globalItem: GlobalItem)

    suspend fun updateGlobalItem(globalItem: GlobalItem)

    suspend fun updateColor(color: String)

    suspend fun getGlobalItems(): Result<List<GlobalItem>>

    suspend fun getGlobalItem(globalItemId: Long): Result<GlobalItem>

    fun observeGlobalItems(): LiveData<Result<List<GlobalItem>>>
}