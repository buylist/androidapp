package ru.buylist.data.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.buylist.data.entity.Collection;

@Dao
public interface CollectionDao {

    @Insert
    void addCollection(Collection collection);

    @Delete
    void deleteCollection(Collection collection);

    @Update
    void updateCollection(Collection collection);

    @Query("SELECT * FROM collections")
    LiveData<List<Collection>> getAllCollection();

    @Query("SELECT * FROM collections WHERE id = :id")
    LiveData<Collection> getCollection(long id);

    @Query("SELECT * FROM collections WHERE type = :type")
    LiveData<List<Collection>> getLiveCollection(String type);

    @Query("SELECT * FROM collections WHERE type = :type")
    List<Collection> getCollection(String type);
}
