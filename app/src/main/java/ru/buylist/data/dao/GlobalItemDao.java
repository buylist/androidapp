package ru.buylist.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
