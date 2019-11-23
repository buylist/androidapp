package ru.buylist.data.db;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;

import ru.buylist.R;
import ru.buylist.data.dao.*;
import ru.buylist.data.entity.*;
import ru.buylist.utils.AppExecutors;
import ru.buylist.utils.CategoryDatabaseWorker;

@Database(entities = {Collection.class, Item.class, GlobalItem.class, Category.class}, version = 1, exportSchema = false)
public abstract class BuyListDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "buyListBase";
    private static BuyListDatabase instance;

    public static BuyListDatabase getInstance(final Context context, final AppExecutors executors) {
        if (instance == null) {
            synchronized (BuyListDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), BuyListDatabase.class,
                            DATABASE_NAME)
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    executors.discIO().execute(() -> {
                                        List<Category> categories = CategoryDatabaseWorker.getStandartCategories(context);
                                        for (Category category : categories) {
                                            getInstance(context, executors).categoryDao().addCategory(category);
                                        }
                                    });
                                }
                            })
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

