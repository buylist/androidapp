package ru.buylist.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

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
    LiveData<List<Collection>> getCollection(String type);
}
