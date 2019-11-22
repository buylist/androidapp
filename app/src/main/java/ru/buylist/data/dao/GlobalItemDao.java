package ru.buylist.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import ru.buylist.data.entity.GlobalItem;

@Dao
public interface GlobalItemDao {

    @Insert
    void addGlobalItem(GlobalItem globalItem);

    @Delete
    void deleteGlobalItem(GlobalItem globalItem);

    @Update
    void updateGlobalItem(GlobalItem globalItem);

    @Query("SELECT * FROM global_items WHERE name = :name")
    GlobalItem getGlobalItem(String name);
}
