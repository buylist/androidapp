package ru.buylist.colection_lists;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import ru.buylist.utils.BuylistApp;
import ru.buylist.data.DataRepository;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;

public class CollectionViewModel extends AndroidViewModel {
    private static final String TAG = "TAG";

    private final DataRepository repository;
    private LiveData<List<Collection>> collectionOfList;
    private LiveData<List<Collection>> collectionOfPattern;
    private LiveData<List<Collection>> collectionOfRecipe;

    public CollectionViewModel(@NonNull Application context) {
        super(context);
        repository = ((BuylistApp) context).getRepository();

        collectionOfList = repository.getCollection(CollectionType.BuyList);
        collectionOfPattern = repository.getCollection(CollectionType.PATTERN);
        collectionOfRecipe = repository.getCollection(CollectionType.RECIPE);
    }

    public LiveData<List<Collection>> getCollectionOfList() {
        Log.i(TAG, "CollectionViewModel get list");
        return collectionOfList;
    }

    public LiveData<List<Collection>> getCollectionOfPattern() {
        return collectionOfPattern;
    }

    public LiveData<List<Collection>> getCollectionOfRecipe() {
        return collectionOfRecipe;
    }

    public void addCollection(Collection collection) {
        repository.addCollection(collection);
        Log.i(TAG, "CollectionViewModel add new collection: " + collection.getId());
    }

    public void deleteCollection(Collection collection) {
        repository.deleteCollection(collection);
        List<Item> items = repository.getItems(collection.getId());
        if (items != null) {
            repository.deleteItems(items);
        }
        Log.i(TAG, "CollectionViewModel delete collection: " + collection.getId());
    }
}
