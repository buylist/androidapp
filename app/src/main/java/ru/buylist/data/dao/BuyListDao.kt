package ru.buylist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.buylist.data.entity.BuyList

@Dao
interface BuyListDao {

    @Insert
    fun insertBuyList(buyList: BuyList)

    @Update
    fun updateBuyList(buyList: BuyList)

    @Delete
    fun deleteBuyList(buyList: BuyList)

    @Delete
    fun deleteSelectedBuyLists(buyLists: List<BuyList>)

    @Query("DELETE FROM buy_lists")
    fun deleteAllBuyLists()

    @Query("SELECT * FROM buy_lists")
    fun getBuyLists(): List<BuyList>

    @Query("SELECT * FROM buy_lists WHERE id = :buyListId")
    fun getBuyList(buyListId: Long): BuyList

    @Query("SELECT * FROM buy_lists")
    fun observeBuyLists(): LiveData<List<BuyList>>

    @Query("SELECT * FROM buy_lists WHERE id = :buyListId")
    fun observeBuyListById(buyListId: Long): LiveData<BuyList>

}