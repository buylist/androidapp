package ru.buylist.buy_list;


import android.app.Application;
import android.content.Context;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.buylist.R;
import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.utils.SingleLiveEvent;


public class BuyListViewModel extends AndroidViewModel {
    private static final String TAG = "TAG";

    // отображаемый список
    public final ObservableList<Item> items = new ObservableArrayList<>();

    // Флаги для отображения/скрытия элементов
    public final ObservableBoolean layoutFieldsShow = new ObservableBoolean(false);
    public final ObservableBoolean fabIsShown = new ObservableBoolean(true);
    public final ObservableBoolean purchasedItemShow = new ObservableBoolean(true);
    public final ObservableBoolean btnPrevCirclesShow = new ObservableBoolean(false);
    public final ObservableBoolean btnNextCirclesShow = new ObservableBoolean(false);

    // Поля ввода данных нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private final Context context;

    private DataRepository repository;
    private TemporaryDataStorage storage;

    // Отслеживание нового товара для открытия NewItemFragment
    private SingleLiveEvent<Long> newItemEvent = new SingleLiveEvent<>();

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в NewItemFragment для перехода в список
    private SingleLiveEvent<Long> returnToListEvent = new SingleLiveEvent<>();

    // Отвечает за открытие диалогового окна "по шаблонам"
    private SingleLiveEvent<Long> patternDialogEvent = new SingleLiveEvent<>();

    // Отвечает за открытие диалогового окна "по рецептам"
    private SingleLiveEvent<Long> recipeDialogEvent = new SingleLiveEvent<>();

    // Открытие
    private SingleLiveEvent<Collection> chooseItemsEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> snackbarText = new SingleLiveEvent<>();


    public BuyListViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        repository = ((BuylistApp) context).getRepository();
        storage = ((BuylistApp) context).getStorage();
    }


    /**
     * get Event
     */

    public SingleLiveEvent<Long> getNewItemEvent() {
        return newItemEvent;
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

    public SingleLiveEvent<Collection> getChooseItemsEvent() {
        return chooseItemsEvent;
    }

    public SingleLiveEvent<Integer> getSnackbarMessage() {
        return snackbarText;
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
        fabIsShown.set(true);
    }

    public void addCategory(Category category) {
        repository.addCategory(category);
        Log.i(TAG, "ShoppingViewModel add category: " + category.getName());
    }

    public Category getCategory(String name) {
        Log.i(TAG, "ShoppingViewModel return category: " + name);
        return repository.getCategory(name);
    }

    public LiveData<List<Category>> getLiveCategories() {
        return repository.getLiveCategories();
    }

    public void setSnackbarText(int msg) {
        snackbarText.setValue(msg);
    }


    /**
     * main
     */

    // itemId == 0 - новый товар, != 0 - редактируемый товар
    public void createNewItem(long itemId) {
        newItemEvent.setValue(itemId);
    }

    // true - товар перечеркивается линией, false - линия удаляется
    public void checkItem(Item item) {
        if (item.isPurchased()) {
            item.setPurchased(false);
        } else {
            item.setPurchased(true);
        }
        repository.updateItem(item);
        fabIsShown.set(true);
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
    public void updateCategory(String categoryName, String color, Item item) {
        Category newCategory = new Category(categoryName, color);
        GlobalItem globalItem = new GlobalItem(item.getName());
        Category category = repository.getCategory(categoryName);

        // если нет в БД
        if (category == null) {
            if (color.equals(CategoryInfo.COLOR)) {
                snackbarText.setValue(R.string.category_color_is_empty);
                return;
            }

            repository.addCategory(newCategory);

            item.setCategory(newCategory.getName());
            item.setCategoryColor(newCategory.getColor());
            globalItem.setCategory(newCategory.getName());
            globalItem.setCategoryColor(newCategory.getColor());
        } else {
            if (!color.equals(CategoryInfo.COLOR)) {
                category.setColor(color);
                item.setCategoryColor(color);
                globalItem.setCategoryColor(color);
                repository.updateCategory(category);
            } else {
                item.setCategoryColor(category.getColor());
                globalItem.setCategoryColor(category.getColor());
            }

            item.setCategory(category.getName());
            globalItem.setCategory(category.getName());
        }

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

            if (items.get(i).getCategory().equals(item.getCategory())) {
                items.get(i).setCategoryColor(item.getCategoryColor());
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

        if (itemsToShow.isEmpty()) {
            fabIsShown.set(true);
        }

        Collections.sort(itemsToShow, new Comparator<Item>() {
            @Override
            public int compare(Item obj1, Item obj2) {
                return obj1.getCategoryColor().compareTo(obj2.getCategoryColor());
            }
        });

        this.items.clear();
        this.items.addAll(itemsToShow);
    }

    // отображение полей для ввода товара
    public void showLayoutFields() {
        layoutFieldsShow.set(true);
        fabIsShown.set(false);
    }

    // скрытие полей для ввода товара
    private void hideNewProductLayout() {
        layoutFieldsShow.set(false);
        fabIsShown.set(true);
    }

    // отображение bottomNavigation + fab
    public void showActivityLayout() {
        fabIsShown.set(true);
    }

    // скрытие bottomNavigation + fab
    public void hideActivityLayout() {
        fabIsShown.set(false);
    }

    public void showHideFab(int dy) {
        if (dy > 0) {
            fabIsShown.set(false);
        } else if (dy < 0) {
            fabIsShown.set(true);
        }
    }

    public void showHideCirclesButton(boolean prevIsShown, boolean nextIsShown) {
        btnPrevCirclesShow.set(prevIsShown);
        btnNextCirclesShow.set(nextIsShown);
    }

    public void openPatternDialog(long collectionId) {
        patternDialogEvent.setValue(collectionId);
    }

    public void openRecipeDialog(long collectionId) {
        recipeDialogEvent.setValue(collectionId);
    }

    public void chooseItemsFrom(Collection collection) {
        chooseItemsEvent.setValue(collection);
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }
}
