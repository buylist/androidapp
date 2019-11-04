package ru.buylist.collection_lists;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import ru.buylist.utils.BuylistApp;
import ru.buylist.data.DataRepository;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;

public class CollectionViewModel extends AndroidViewModel {
    private static final String TAG = "TAG";

    // Поля ввода имени листа / шаблона / рецепта
    public final ObservableField<String> buyListName = new ObservableField<>();
    public final ObservableField<String> patterName = new ObservableField<>();
    public final ObservableField<String> recipeName = new ObservableField<>();

    // Флаги для отображения / скрытия полей ввода
    public final ObservableBoolean layoutBuyListShow = new ObservableBoolean(false);
    public final ObservableBoolean layoutPatternListShow = new ObservableBoolean(false);
    public final ObservableBoolean layoutRecipeListShow = new ObservableBoolean(false);

    // Флаги для отображения / скрытия recyclerView
    public final ObservableBoolean recyclerBuyListShow = new ObservableBoolean(false);
    public final ObservableBoolean recyclerPatternListShow = new ObservableBoolean(false);
    public final ObservableBoolean recyclerRecipeListShow = new ObservableBoolean(false);


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

    public void updateCollection(Collection collection) {
        repository.updateCollection(collection);
    }

    public void deleteCollection(Collection collection) {
        repository.deleteCollection(collection);
        List<Item> items = repository.getItems(collection.getId());
        if (items != null) {
            repository.deleteItems(items);
        }
        Log.i(TAG, "CollectionViewModel delete collection: " + collection.getId());
    }

    public void editCollection(Collection collection) {
        switch (collection.getType()) {
            case CollectionType.BuyList:
                layoutBuyListShow.set(true);
                buyListName.set(collection.getTitle());
                break;
            case CollectionType.PATTERN:
                layoutPatternListShow.set(true);
                patterName.set(collection.getTitle());
                break;
            case CollectionType.RECIPE:
                layoutRecipeListShow.set(true);
                recipeName.set(collection.getTitle());
                break;
            default:
                break;
        }
    }

    public void openOrCloseCards(String type) {
        switch (type) {
            case CollectionType.BuyList:
                layoutBuyListShow.set(false);
                changeRecyclerVisibility(recyclerBuyListShow);
                break;
            case CollectionType.PATTERN:
                layoutPatternListShow.set(false);
                changeRecyclerVisibility(recyclerPatternListShow);
                break;
            case CollectionType.RECIPE:
                layoutRecipeListShow.set(false);
                changeRecyclerVisibility(recyclerRecipeListShow);
                break;
        }
        clearFields();
    }

    private void changeRecyclerVisibility(ObservableBoolean show) {
        if (!show.get()) {
            show.set(true);
        } else {
            show.set(false);
        }
    }

    public void addCollection(String type) {
        switch (type) {
            case CollectionType.BuyList:
                layoutBuyListShow.set(true);
                recyclerBuyListShow.set(true);
                break;
            case CollectionType.PATTERN:
                layoutPatternListShow.set(true);
                recyclerPatternListShow.set(true);
                break;
            case CollectionType.RECIPE:
                layoutRecipeListShow.set(true);
                recyclerRecipeListShow.set(true);
                break;
        }
    }

    public void saveCollection(long collectionId, String type) {
        Collection collection = (collectionId == 0 ? new Collection() : new Collection(collectionId));
        switch (type) {
            case CollectionType.BuyList:
                collection.setTitle(buyListName.get());
                collection.setType(CollectionType.BuyList);
                break;
            case CollectionType.PATTERN:
                collection.setTitle(patterName.get());
                collection.setType(CollectionType.PATTERN);
                break;
            case CollectionType.RECIPE:
                collection.setTitle(recipeName.get());
                collection.setType(CollectionType.RECIPE);
                break;
        }

        if (collection.isEmpty()) {
            clearFields();
            layoutBuyListShow.set(false);
            layoutPatternListShow.set(false);
            layoutRecipeListShow.set(false);
            return;
        }

        if (collectionId == 0) {
            repository.addCollection(collection);
        } else {
            repository.updateCollection(collection);
        }

        clearFields();
        layoutBuyListShow.set(false);
        layoutPatternListShow.set(false);
        layoutRecipeListShow.set(false);
    }

    private void clearFields() {
        buyListName.set("");
        patterName.set("");
        recipeName.set("");
    }
}
