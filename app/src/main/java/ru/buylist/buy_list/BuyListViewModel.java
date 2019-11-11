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

import ru.buylist.data.TemporaryDataStorage;
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
    public final ObservableBoolean layoutFieldsShow = new ObservableBoolean(false);
    public final ObservableBoolean fabAddItemShow = new ObservableBoolean(true);
    public final ObservableBoolean fabVisibilityShow = new ObservableBoolean(true);
    public final ObservableBoolean bottomShow = new ObservableBoolean(true);
    public final ObservableBoolean purchasedItemShow = new ObservableBoolean(true);

    // Поля ввода данных нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private final Context context;

    private DataRepository repository;
    private TemporaryDataStorage storage;

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> newCategoryEvent = new SingleLiveEvent<>();

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    private SingleLiveEvent<Long> returnToListEvent = new SingleLiveEvent<>();

    // Отвечает за открытие диалогового окна "по шаблонам"
    private SingleLiveEvent<Long> patternDialogEvent = new SingleLiveEvent<>();

    // Отвечает за открытие диалогового окна "по рецептам"
    private SingleLiveEvent<Long> recipeDialogEvent = new SingleLiveEvent<>();


    public BuyListViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        repository = ((BuylistApp) context).getRepository();
        storage = ((BuylistApp) context).getStorage();
    }


    /**
     * get Event
     */

    public SingleLiveEvent<Long> getNewCategoryEvent() {
        return newCategoryEvent;
    }

    public SingleLiveEvent<Long> getReturnToListEvent() {
        return returnToListEvent;
    }

    public SingleLiveEvent<Long> getPatternDialogEvent() {
        return patternDialogEvent;
    }

    public SingleLiveEvent<Long> getRecipeDialogEvent() {
        return recipeDialogEvent;
    }

    /**
     * get LiveData / work with repository
     */

    public LiveData<Collection> getCollection(long collectionId) {
        Log.i(TAG, "ShoppingViewModel get live collection: " + collectionId);
        return repository.getCollection(collectionId);
    }

    public void updateCollection(Collection collection) {
        repository.updateCollection(collection);
        Log.i(TAG, "ShoppingViewModel update collection: " + collection.getId());
    }

    public LiveData<List<Item>> getItems(long id) {
        Log.i(TAG, "ShoppingViewModel get live items of collectionId: " + id);
        return repository.getLiveItems(id);
    }

    public List<Item> getAllItems() {
        return repository.getAllItems();
    }

    public Item getItem(long id) {
        Log.i(TAG, "ShoppingViewModel get item: " + id);
        return repository.getItem(id);
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }

    public void addCategory(Category category) {
        repository.addCategory(category);
        Log.i(TAG, "ShoppingViewModel add category: " + category.getName());
    }

    public Category getCategory(String name) {
        Log.i(TAG, "ShoppingViewModel return category: " + name);
        return repository.getCategory(name);
    }


    /**
     * main
     */

    // новый товар добавляет в базу, существующий - обновляет
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

        // новый товар добавляется в базу, редактируемый - обновляется
        if (itemId == 0) {
            repository.addItem(item);
        } else {
            repository.updateItem(item);
        }

        // открывает CategoryFragment, если товара нет в глобальной базе
        if (!isInGlobalDatabase(item)) {
            newCategoryEvent.setValue(item.getId());
        }

        clearFields();
        hideNewProductLayout(targetField);
    }

    // проверка на наличие товара в глобальной базе
    // если товар есть в глобальной базе - цепляем категорию и цвет категории
    private boolean isInGlobalDatabase(Item item) {
        GlobalItem globalItem = repository.getGlobalItem(item.getName());
        if (globalItem == null || globalItem.getName() == null) {
            return false;
        } else {
            item.setCategory(globalItem.getCategory());
            item.setCategoryColor(globalItem.getColorCategory());
            repository.updateItem(item);
            return true;
        }
    }

    // true - товар перечеркивается линией, false - линия удаляется
    public void checkItem(Item item) {
        if (item.isPurchased()) {
            item.setPurchased(false);
        } else {
            item.setPurchased(true);
        }
        repository.updateItem(item);
    }

    // отображение полей для редактирования товара
    public void editItem(Item item) {
        layoutFieldsShow.set(true);
        itemName.set(item.getName());
        quantity.set(item.getQuantity());
        unit.set(item.getUnit());
        Log.i(TAG, "ShoppingViewModel edit item: " + item.getId());
    }

    // используется, когда пользователем была выбрана категория для нового товара
    // товару присваивается категория, цвет категории и обновляется в БД
    // создается новый глобальный товар и добавляется в БД
    public void updateCategory(String categoryName, Item item) {
        Category category = repository.getCategory(categoryName);
        GlobalItem globalItem = new GlobalItem(
                item.getName(), item.getCategory(), item.getCategoryColor());

        item.setCategory(category.getName());
        item.setCategoryColor(category.getColor());
        globalItem.setCategory(category.getName());
        globalItem.setCategoryColor(category.getColor());

        repository.updateItem(item);
        repository.addGlobalItem(globalItem);
        updateItemsList(item);

        // возврат в список / шаблон / рецепт
        returnToListEvent.setValue(item.getCollectionId());
    }

    // проверка на наличие в общем списке товаров товара с идентичным именем
    // при совпадении и при отсутствии у такого товара категории - цепляет у только что созданного
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

    // используется, когда пользователь не установил категорию для товара (кнопка Пропустить)
    public void skipCategory(Item item) {
        returnToListEvent.setValue(item.getCollectionId());
    }

    // true - отображение товаров к покупке, false - отображение всех товаров
    public void updateFiltering() {
        if (purchasedItemShow.get()) {
            purchasedItemShow.set(false);
        } else {
            purchasedItemShow.set(true);
        }
    }

    // сортировка отображаемых товаров
    public void loadItems(List<Item> items) {
        List<Item> itemsToShow = new ArrayList<>();
        if (!purchasedItemShow.get()) {
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

    // отображение полей для ввода товара
    public void showLayoutFields(EditText targetField) {
        layoutFieldsShow.set(true);
        fabAddItemShow.set(false);
        fabVisibilityShow.set(false);
        bottomShow.set(false);
        KeyboardUtils.showKeyboard(targetField, context);
    }

    // скрытие полей для ввода товара
    public void hideNewProductLayout(EditText targetField) {
        layoutFieldsShow.set(false);
        fabAddItemShow.set(true);
        fabVisibilityShow.set(true);
        bottomShow.set(true);
        KeyboardUtils.hideKeyboard(targetField, context);
    }

    // отображение bottomNavigation + fab
    public void showActivityLayout() {
        fabVisibilityShow.set(true);
        fabAddItemShow.set(true);
        bottomShow.set(true);
    }

    // скрытие bottomNavigation + fab
    public void hideActivityLayout() {
        fabVisibilityShow.set(false);
        fabAddItemShow.set(false);
        bottomShow.set(false);
    }

    public void openPatternDialog(long collectionId) {
        patternDialogEvent.setValue(collectionId);
    }

    public void openRecipeDialog(long collectionId) {
        recipeDialogEvent.setValue(collectionId);
    }

    public void transferItems(List<Item> items, long collectionId) {
        for (int i = 0; i < items.size(); i++) {
            Item item = new Item(i, collectionId, items.get(i).getName(), items.get(i).getCategory(),
                    items.get(i).getCategoryColor(), items.get(i).getQuantity(),
                    items.get(i).getUnit());
            repository.addItem(item);
        }
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }
}
