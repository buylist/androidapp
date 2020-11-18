package ru.buylist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.buylist.data.entity.GlobalItem

@Dao
interface GlobalItemDao {

    @Insert
    suspend fun insertGlobalItems(globalItems: List<GlobalItem>)

    @Insert
    suspend fun insertGlobalItem(globalItem: GlobalItem)

    @Update
    suspend fun updateGlobalItem(globalItem: GlobalItem)

    @Query("UPDATE global_items SET color = :color")
    suspend fun updateColor(color: String)

    @Query("SELECT * FROM global_items")
    suspend fun getGlobalItems(): List<GlobalItem>

    @Query("SELECT * FROM global_items WHERE id = :globalItemId")
    suspend fun getGlobalItem(globalItemId: Long): GlobalItem

    @Query("SELECT * FROM global_items")
    fun observeGlobalItems(): LiveData<List<GlobalItem>>

}