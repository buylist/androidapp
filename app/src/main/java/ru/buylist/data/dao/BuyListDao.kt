package ru.buylist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.buylist.data.entity.BuyList

@Dao
interface BuyListDao {

    @Insert
    suspend fun insertBuyList(buyList: BuyList)

    @Update
    suspend fun updateBuyList(buyList: BuyList)

    @Delete
    suspend fun deleteBuyList(buyList: BuyList)

    @Delete
    suspend fun deleteSelectedBuyLists(buyLists: List<BuyList>)

    @Query("DELETE FROM buy_lists")
    suspend fun deleteAllBuyLists()

    @Query("SELECT * FROM buy_lists")
    suspend fun getBuyLists(): List<BuyList>

    @Query("SELECT * FROM buy_lists WHERE id = :buyListId")
    suspend fun getBuyList(buyListId: Long): BuyList

    @Query("SELECT * FROM buy_lists")
    fun observeBuyLists(): LiveData<List<BuyList>>

    @Query("SELECT items FROM buy_lists WHERE id = :buyListId")
    fun observeBuyListById(buyListId: Long): LiveData<String>

    @Query("UPDATE buy_lists SET items =:products WHERE id =:buyListId")
    suspend fun updateProducts(buyListId: Long, products: String)

}