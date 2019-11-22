package ru.buylist.data.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import ru.buylist.data.dao.*;
import ru.buylist.data.entity.*;

@Database(entities = {Collection.class, Item.class, GlobalItem.class, Category.class}, version = 1, exportSchema = false)
public abstract class BuyListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "buyListBase";
    private static BuyListDatabase instance;

    public static BuyListDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (BuyListDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), BuyListDatabase.class,
                            DATABASE_NAME)
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract CollectionDao collectionDao();

    public abstract ItemDao itemDao();

    public abstract GlobalItemDao globalItemDao();

    public abstract CategoryDao categoryDao();
}

