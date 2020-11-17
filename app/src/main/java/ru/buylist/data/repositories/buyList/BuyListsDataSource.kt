package ru.buylist.data.repositories.buyList

import androidx.lifecycle.LiveData
import ru.buylist.data.entity.BuyList
import ru.buylist.data.Result

interface BuyListsDataSource {

    suspend fun saveBuyList(buyList: BuyList)

    suspend fun updateBuyList(buyList: BuyList)

    suspend fun deleteBuyList(buyList: BuyList)

    suspend fun deleteSelectedBuyList(buyLists: List<BuyList>)

    suspend fun deleteAllBuyList()

    suspend fun getBuyLists(): Result<List<BuyList>>

    suspend fun getBuyList(buyListId: Long): Result<BuyList>

    fun observeBuyLists(): LiveData<Result<List<BuyList>>>

    fun observeBuyList(buyListId: Long?): LiveData<Result<String>>

    suspend fun updateProducts(buyListId: Long?, products: String)

    suspend fun getProducts(buyListId: Long?): Result<String>
}