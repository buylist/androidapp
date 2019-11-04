package ru.buylist.data;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import ru.buylist.utils.AppExecutors;
import ru.buylist.data.db.BuyListDatabase;
import ru.buylist.data.entity.*;

public class DataRepository {
    private static final String TAG = "DataRepository";

    private static DataRepository repository;
    private final BuyListDatabase database;
    private AppExecutors executors;

    private DataRepository(final BuyListDatabase database, AppExecutors executors) {
        this.database = database;
        this.executors = executors;
    }

    public static DataRepository get(BuyListDatabase database, AppExecutors executors) {
        if (repository == null) {
            synchronized (DataRepository.class) {
                if (repository == null) {
                    repository = new DataRepository(database, executors);
                }
            }
        }
        return repository;
    }


    /**
     * Методы для коллекции списков
     **/
    public void addCollection(final Collection collection) {
        Runnable saveRunnable = () -> database.collectionDao().addCollection(collection);
        executors.discIO().execute(saveRunnable);
        Log.i(TAG, "Repository add new collection: " + collection.getTitle());
    }

    public void deleteCollection(final Collection collection) {
        Runnable deleteRunnable = () -> database.collectionDao().deleteCollection(collection);
        executors.discIO().execute(deleteRunnable);
        Log.i(TAG, "Repository delete collection: " + collection.getTitle());
    }

    public void updateCollection(final Collection collection) {
        Runnable updateRunnable = () -> database.collectionDao().updateCollection(collection);
        executors.discIO().execute(updateRunnable);
        Log.i(TAG, "Repository update collection: " + collection.getTitle());
    }

    public LiveData<List<Collection>> getCollections() {
        Log.i(TAG, "Repository return collections");
        return database.collectionDao().getAllCollection();
    }

    public LiveData<List<Collection>> getCollection(String type) {
        Log.i(TAG, "Repository return Collection type: " + type);
        return database.collectionDao().getLiveCollection(type);
    }

    public LiveData<Collection> getCollection(final long id) {
        Log.i(TAG, "Repository return collection: " + database.collectionDao().getCollection(id));
        return database.collectionDao().getCollection(id);
    }


    /**
     * Методы для работы с товарами
     **/

    public void addItem(final Item item) {
        Log.i(TAG, "Repository try to add new item: " + item.getId());
        Runnable addRunnable = () -> database.itemDao().addItem(item);
        executors.discIO().execute(addRunnable);
        Log.i(TAG, "Repository add new item: " + item.getId());
    }

    public void deleteItems(final List<Item> items) {
        Runnable deleteRunnable = () -> database.itemDao().deleteItems(items);
        executors.discIO().execute(deleteRunnable);
        Log.i(TAG, "Repository delete items by type");
    }

    public void deleteItem(final Item item) {
        Runnable deleteRunnable = () -> database.itemDao().deleteItem(item);
        executors.discIO().execute(deleteRunnable);
        Log.i(TAG, "Repository delete item: " + item.getId());
    }

    public void updateItem(final Item item) {
        Runnable updateRunnable = () -> database.itemDao().updateItem(item);
        executors.discIO().execute(updateRunnable);
        Log.i(TAG, "Repository update item: " + item.getId());
    }

    public Item getItem(final long id) {
        Log.i(TAG, "Repository getItem " + id);
        FutureTask<Item> future = new FutureTask<>(() -> database.itemDao().getItem(id));
        executors.discIO().execute(future);
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Используется, когда вместе с удалением списка необходимо удалить все относящиеся к нему товары
    public List<Item> getItems(long collectionId) {
        FutureTask<List<Item>> task = new FutureTask<>(() -> database.itemDao().getItems(collectionId));
        executors.discIO().execute(task);
        try {
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Item>> getLiveItems(long collectionId) {
        Log.i(TAG, "Repository return live items, size: ");
        return database.itemDao().getLiveItems(collectionId);
    }


    /**
     * Методы для работы с товарами из глобальной базы
     */

    public void addGlobalItem(final GlobalItem globalItem) {
        Runnable addRunnable = () -> database.globalItemDao().addGlobalItem(globalItem);
        executors.discIO().execute(addRunnable);
        Log.i(TAG, "Repository add global item: " + globalItem.getName());
    }

    public GlobalItem getGlobalItem(final String name) {
        FutureTask<GlobalItem> test = new FutureTask<>(() -> database.globalItemDao().getGlobalItem(name));
        executors.discIO().execute(test);
        try {
            return test.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Метода для работы с категориями
     */

    public void addCategory(final Category category) {
        Runnable addRunnable = () -> database.categoryDao().addCategory(category);
        executors.discIO().execute(addRunnable);
        Log.i(TAG, "Repository add category: " + category.getName());
    }

    public Category getCategory(String name) {
        FutureTask<Category> task = new FutureTask<>(() -> database.categoryDao().getCategory(name));
        executors.discIO().execute(task);
        try {
            return task.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
