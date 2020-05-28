package ru.buylist.buy_list;


import android.app.Application;
import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;

import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.utils.SingleLiveEvent;


public class NewItemViewModel extends AndroidViewModel {
    private static final String TAG = "TAG";

    // Флаги для отображения/скрытия элементов
    public final ObservableBoolean btnPrevCirclesShow = new ObservableBoolean(false);
    public final ObservableBoolean btnNextCirclesShow = new ObservableBoolean(false);
    public final ObservableBoolean keyboardShow = new ObservableBoolean(false);

    // Поля ввода данных нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private DataRepository repository;
    private TemporaryDataStorage storage;

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в NewItemFragment для перехода в список
    private SingleLiveEvent<Long> returnToListEvent = new SingleLiveEvent<>();



    public NewItemViewModel(Application context) {
        super(context);
        context.getApplicationContext();
        repository = ((BuylistApp) context).getRepository();
        storage = ((BuylistApp) context).getStorage();
    }


    /**
     * get Event
     */
    public SingleLiveEvent<Long> getReturnToListEvent() {
        return returnToListEvent;
    }


    /**
     * get LiveData / work with repository
     */
    public Item getItem(long id) {
        Log.i(TAG, "ShoppingViewModel get item: " + id);
        return repository.getItem(id);
    }


    /**
     * main
     */

    // новый товар добавляет в базу, существующий - обновляет
    public void saveItem(long collectionId, long itemId, String color) {
        // id != 0 при редактировании товара, создавать новый не требуется
        Item item = (itemId == 0 ? new Item() : new Item(itemId));
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем layout
            clearFields();
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get() + " ");
        item.setUnit(unit.get());
        item.setCategoryColor(color);

        // новый товар добавляется в базу, редактируемый - обновляется
        if (itemId == 0) {
            repository.addItem(item);
        } else {
            repository.updateItem(item);
        }

        addToGlobalDatabase(item);
        clearFields();
    }

    public void showHideCirclesButton(boolean prevIsShown, boolean nextIsShown) {
        btnPrevCirclesShow.set(prevIsShown);
        btnNextCirclesShow.set(nextIsShown);
    }

    public void showHideKeyboard(boolean show) {
        keyboardShow.set(show);
    }

    private void addToGlobalDatabase(Item item) {
        GlobalItem globalItem = repository.getGlobalItem(item.getName());
        if (globalItem == null || globalItem.getName() == null) {
            GlobalItem newItem = new GlobalItem(
                    item.getName(),
                    item.getCategory(),
                    item.getCategoryColor()
            );
            repository.addGlobalItem(newItem);
        }
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }
}
