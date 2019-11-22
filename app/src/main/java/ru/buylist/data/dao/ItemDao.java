package ru.buylist.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.buylist.data.entity.Item;

@Dao
public interface ItemDao {

    @Insert
    void addItem(Item item);

    @Delete
    void deleteItem(Item item);

    @Delete
    void deleteItems(List<Item> items);

    @Update
    void updateItem(Item item);

    @Query("SELECT * FROM items WHERE collectionId = :collectionId")
    LiveData<List<Item>> getLiveItems(long collectionId);

    @Query("SELECT * FROM items WHERE collectionId = :collectionId")
    List<Item> getItems(long collectionId);

    @Query("SELECT * FROM items")
    List<Item> getAllItems();

    @Query("SELECT * FROM items WHERE id = :id")
    LiveData<Item> getLiveItem(long id);

    @Query("SELECT * FROM items WHERE id = :id")
    Item getItem(long id);
}
