package ru.buylist.pattern_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.widget.EditText;

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
    private SingleLiveEvent<Long> itemCreated = new SingleLiveEvent<>();

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    public SingleLiveEvent<Long> categoryAdded = new SingleLiveEvent<>();

    private DataRepository repository;

    public PatternListViewModel(Application context) {
        super(context);
        repository = ((BuylistApp) context.getApplicationContext()).getRepository();
    }

    SingleLiveEvent<Long> getItemCreated() {
        return itemCreated;
    }

    SingleLiveEvent<Long> getCategoryAdded() {
        return categoryAdded;
    }

    LiveData<Collection> getCollection(long id) {
        return repository.getCollection(id);
    }

    LiveData<List<Item>> getItems(long id) {
        return repository.getLiveItems(id);
    }

    // onFabClick
    public void addNewItem() {
        layoutFieldsShow.set(true);
        bottomShow.set(false);
    }

    public void saveItem(long collectionId) {
        Item item = new Item();
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, ничего не делаем
            layoutFieldsShow.set(false);
            bottomShow.set(true);
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get());
        item.setUnit(unit.get());
        repository.addItem(item);

        if (isNewItem(item)) {
            createItem(item);
        }

        layoutFieldsShow.set(false);
        bottomShow.set(true);
        clearFields();
    }

    public void savecategory(long productId) {

    }

    // проверка на наличие в GlobalProductTable
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

    // добавление в базу и т.к. товар новый - вызов event для открытия фрагмента с выбором категории
    private void createItem(Item item) {
        itemCreated.setValue(item.getId());
    }

    // обновление товара в базе
    public void updateItem(long itemId) {
        Item item = repository.getItem(itemId);
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем layout
            clearFields();
            layoutFieldsShow.set(false);
            bottomShow.set(true);
            return;
        }

        item.setQuantity(quantity.get());
        item.setUnit(unit.get());
        repository.updateItem(item);

        if (isNewItem(item)) {
            itemCreated.setValue(item.getId());
        }
        clearFields();
        layoutFieldsShow.set(false);
        bottomShow.set(true);
    }

    public void loadItems(List<Item> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }

    public void editItem(Item item) {
        layoutFieldsShow.set(true);
        bottomShow.set(false);
        itemName.set(item.getName());
        quantity.set(item.getQuantity());
        unit.set(item.getUnit());
    }

    public void deleteItem(Item item) {
        repository.deleteItem(item);
    }
}
