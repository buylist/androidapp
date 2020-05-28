package ru.buylist.data.dao

import androidx.room.*
import ru.buylist.data.entity.GlobalItem

@Dao
interface GlobalItemDao {

    @Insert
    fun insertGlobalItem(globalItem: GlobalItem)

    @Update
    fun updateGlobalItem(globalItem: GlobalItem)

    @Query("UPDATE global_items SET category = :category")
    fun updateGlobalItemCategory(category: String)

    @Query("UPDATE global_items SET categoryColor = :color")
    fun updateGlobalItemColor(color: String)

    @Query("SELECT * FROM global_items")
    fun getGlobalItems(): List<GlobalItem>

    @Query("SELECT * FROM global_items WHERE id = :globalItemId")
    fun getGlobalItem(globalItemId: Long): GlobalItem

}