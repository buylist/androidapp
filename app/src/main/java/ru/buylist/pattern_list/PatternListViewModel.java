package ru.buylist.pattern_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import java.util.List;

import ru.buylist.data.entity.Collection;
import ru.buylist.utils.BuylistApp;
import ru.buylist.data.DataRepository;
import ru.buylist.utils.SingleLiveEvent;
import ru.buylist.data.entity.GlobalItem;
import ru.buylist.data.entity.Item;


public class PatternListViewModel extends AndroidViewModel {

    // отображаемый список
    public final ObservableList<Item> items = new ObservableArrayList<>();

    // Флаги для отображения/скрытия элементов разметки
    public final ObservableBoolean btnToMoveShow = new ObservableBoolean(false);
    public final ObservableBoolean layoutFieldsShow = new ObservableBoolean(false);
    public final ObservableBoolean bottomShow = new ObservableBoolean(true);

    // Поля для ввода нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> newCategoryEvent = new SingleLiveEvent<>();

    // Открытие диалогового окна
    private SingleLiveEvent<String> dialogEvent = new SingleLiveEvent<>();

    private DataRepository repository;



    public PatternListViewModel(Application context) {
        super(context);
        repository = ((BuylistApp) context.getApplicationContext()).getRepository();
    }

    /**
     *  get Event
    * */

    SingleLiveEvent<Long> getNewCategoryEvent() {
        return newCategoryEvent;
    }

    SingleLiveEvent<String> getDialogEvent() {
        return dialogEvent;
    }


    /**
     *  get LiveData / work with repository
    * */

    LiveData<Collection> getCollection(long id) {
        return repository.getCollection(id);
    }

    LiveData<List<Collection>> getCollections(String type) {
        return repository.getCollection(type);
    }

    LiveData<List<Item>> getItems(long id) {
        return repository.getLiveItems(id);
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }


    /**
     *  main
    * */

    // onFabClick
    public void addNewItem() {
        layoutFieldsShow.set(true);
        bottomShow.set(false);
    }

    public void saveItem(long collectionId, long itemId) {
        Item item = (itemId == 0 ? new Item() : new Item(itemId));
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем поля
            clearFields();
            layoutFieldsShow.set(false);
            bottomShow.set(true);
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get());
        item.setUnit(unit.get());

        // новый товар добавляем в базу, редактируемый - обновляем
        if (itemId == 0) {
            repository.addItem(item);
        } else {
            repository.updateItem(item);
        }

        // если товар новый - открытие фрагмента для выбора категории
        if (isNewItem(item)) {
            newCategoryEvent.setValue(item.getId());
        }

        layoutFieldsShow.set(false);
        bottomShow.set(true);
        clearFields();
    }

    // проверка на наличие в GlobalProductTable
    // если товар уже есть в глобальной базе - цепляем категорию и цвет
    private boolean isNewItem(Item item) {
        GlobalItem globalItem = repository.getGlobalItem(item.getName());
        if (globalItem == null || globalItem.getName() == null) {
            return true;
        } else {
            item.setCategory(globalItem.getCategory());
            item.setCategoryColor(globalItem.getColorCategory());
            repository.updateItem(item);
            return false;
        }
    }

    // при добавлении нового товара список обновляется
    public void loadItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }

    // отображение полей для редактирования товара
    public void editItem(Item item) {
        layoutFieldsShow.set(true);
        bottomShow.set(false);
        itemName.set(item.getName());
        quantity.set(item.getQuantity());
        unit.set(item.getUnit());
    }

    public void openDialog() {
        dialogEvent.call();
        btnToMoveShow.set(false);
    }
}
