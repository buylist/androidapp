package ru.buylist.buy_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import ru.buylist.utils.BuylistApp;
import ru.buylist.data.DataRepository;
import ru.buylist.utils.KeyboardUtils;
import ru.buylist.utils.SingleLiveEvent;
import ru.buylist.data.entity.*;


public class BuyListViewModel extends AndroidViewModel {
    private static final String TAG = "TAG";

    // отображаемый список
    public final ObservableList<Item> items = new ObservableArrayList<>();

    // Флаги для отображения/скрытия элементов
    public final ObservableBoolean layoutNewProductVisibility = new ObservableBoolean(false);
    public final ObservableBoolean fabNewProductVisibility = new ObservableBoolean(true);
    public final ObservableBoolean fabProductsVisibility = new ObservableBoolean(true);
    public final ObservableBoolean bottomNavigationVisibility = new ObservableBoolean(true);
    public final ObservableBoolean purchasedProductsVisibility = new ObservableBoolean(true);

    // Поля ввода данных нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private final Context context;

    private DataRepository repository;

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> newCategoryEvent = new SingleLiveEvent<>();

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    private SingleLiveEvent<Long> addProductEvent = new SingleLiveEvent<>();

    public BuyListViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        repository = ((BuylistApp) context).getRepository();
    }

    SingleLiveEvent<Long> getNewCategoryEvent() {
        return newCategoryEvent;
    }

    SingleLiveEvent<Long> getAddProductEvent() {
        return addProductEvent;
    }

    public LiveData<Collection> getCollection(long collectionId) {
        Log.i(TAG, "ShoppingViewModel get live collection: " + collectionId);
        return repository.getCollection(collectionId);
    }

    public Item getItem(long id) {
        Log.i(TAG, "ShoppingViewModel get item: " + id);
        return repository.getItem(id);
    }

    public LiveData<List<Item>> getItems(long id) {
        Log.i(TAG, "ShoppingViewModel get live items of collectionId: " + id);
        return repository.getLiveItems(id);
    }

    public void updateCollection(Collection collection) {
        repository.updateCollection(collection);
        Log.i(TAG, "ShoppingViewModel update collection: " + collection.getId());
    }

    public void saveItem(EditText targetField, long collectionId, long itemId) {
        // id != 0 при редактировании товара, создавать новый не требуется
        Item item = (itemId == 0 ? new Item() : new Item(itemId));
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем layout
            clearFields();
            hideNewProductLayout(targetField);
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get());
        item.setUnit(unit.get());

        if (!isInGlobalDatabase(item)) {
            repository.addItem(item);
            newCategoryEvent.setValue(item.getId());
        } else {
            repository.addItem(item);
        }
        clearFields();
        hideNewProductLayout(targetField);
    }

    //проверка на наличие товара в глобальной базе
    private boolean isInGlobalDatabase(Item item) {
        GlobalItem globalItem = repository.getGlobalItem(item.getName());
        if (globalItem == null || globalItem.getName() == null) {
            return false;
        } else {
            item.setCategory(globalItem.getCategory());
            item.setCategoryColor(globalItem.getColorCategory());
            return true;
        }
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }

    // true - товар перечеркивается линией, false - линия удаляется
    public void checkItem(Item item) {
        if (item.isPurchased()) {
            item.setPurchased(false);
        } else {
            item.setPurchased(true);
        }
        makeAction(Action.UPDATE, item);
    }

    public void makeAction(Action action, Item item) {
        switch (action) {
            case ADD:
                repository.addItem(item);
                Log.i(TAG, "ShoppingViewModel add new item: " + item.getId());
                break;
            case UPDATE:
                repository.updateItem(item);
                Log.i(TAG, "ShoppingViewModel update item: " + item.getId());
                break;
            case DELETE:
                Log.i(TAG, "ShoppingViewModel delete item: " + item.getId());
                repository.deleteItem(item);
                break;
            default:
                break;
        }
    }

    public void editItem(Item item) {
        layoutNewProductVisibility.set(true);
        itemName.set(item.getName());
        quantity.set(item.getQuantity());
        unit.set(item.getUnit());
        Log.i(TAG, "ShoppingViewModel edit item: " + item.getId());
    }

    public void addCategory(Category category) {
        repository.addCategory(category);
        Log.i(TAG, "ShoppingViewModel add category: " + category.getName());
    }

    public Category getCategory(String name) {
        Log.i(TAG, "ShoppingViewModel return category: " + name);
        return repository.getCategory(name);
    }

    public void updateCategory(String categoryName, Item item) {
        Category category = repository.getCategory(categoryName);
        GlobalItem globalItem = new GlobalItem(
                item.getId(), item.getName(), item.getCategory(), item.getCategoryColor());

        item.setCategory(category.getName());
        item.setCategoryColor(category.getColor());
        globalItem.setCategory(category.getName());
        globalItem.setCategoryColor(category.getColor());

        repository.updateItem(item);
        repository.addGlobalItem(globalItem);
        updateItemsList(item);
        addProductEvent.setValue(item.getCollectionId());
        Log.i(TAG, "ShoppingViewModel set new productEvent");
    }

    private void updateItemsList(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(item.getName())) {
                if (items.get(i).getCategory() == null) {
                    items.get(i).setCategory(item.getCategory());
                }
                if (items.get(i).getCategoryColor() == null) {
                    items.get(i).setCategoryColor(item.getCategoryColor());
                }
                repository.updateItem(items.get(i));
            }
        }
    }

    public void skipCategory(long collectionId) {
        addProductEvent.setValue(collectionId);
        Log.i(TAG, "ShoppingViewModel set new product event: skip");
    }

    // true - отображение товаров к покупке, false - отображение всех товаров
    public void updateFiltering() {
        if (purchasedProductsVisibility.get()) {
            purchasedProductsVisibility.set(false);
        } else {
            purchasedProductsVisibility.set(true);
        }
    }

    // сортировка отображаемых товаров
    public void loadItems(List<Item> items) {
        List<Item> itemsToShow = new ArrayList<>();
        if (!purchasedProductsVisibility.get()) {
            itemsToShow.addAll(items);
        } else {
            for (Item item : items) {
                if (!item.isPurchased()) {
                    itemsToShow.add(item);
                }
            }
        }
        this.items.clear();
        this.items.addAll(itemsToShow);
    }

    public void showNewProductLayout(EditText targetField) {
        layoutNewProductVisibility.set(true);
        fabNewProductVisibility.set(false);
        fabProductsVisibility.set(false);
        bottomNavigationVisibility.set(false);
        KeyboardUtils.showKeyboard(targetField, context);
    }

    public void hideNewProductLayout(EditText targetField) {
        layoutNewProductVisibility.set(false);
        fabNewProductVisibility.set(true);
        fabProductsVisibility.set(true);
        bottomNavigationVisibility.set(true);
        KeyboardUtils.hideKeyboard(targetField, context);
    }

    public void showActivityLayout() {
        fabProductsVisibility.set(true);
        fabNewProductVisibility.set(true);
        bottomNavigationVisibility.set(true);
    }

    public void hideActivityLayout() {
        fabProductsVisibility.set(false);
        fabNewProductVisibility.set(false);
        bottomNavigationVisibility.set(false);
    }



}
