package ru.buylist.pattern_list;

import android.app.Application;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import ru.buylist.R;
import ru.buylist.collection_lists.CollectionType;
import ru.buylist.data.TemporaryDataStorage;
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
    public final ObservableBoolean fabShow = new ObservableBoolean(true);

    // Поля для ввода нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    // Отслеживание нового товара для открытия NewItemFragment
    private SingleLiveEvent<Long> newItemEvent = new SingleLiveEvent<>();

    // Открытие диалогового окна
    private SingleLiveEvent<String> dialogEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Collection> returnToBuyListEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> snackbarText = new SingleLiveEvent<>();

    private DataRepository repository;
    private TemporaryDataStorage storage;



    public PatternListViewModel(Application context) {
        super(context);
        repository = ((BuylistApp) context.getApplicationContext()).getRepository();
        storage = ((BuylistApp) context.getApplicationContext()).getStorage();
    }

    /**
     *  get Event
    * */

    SingleLiveEvent<Long> getNewItemEvent() {
        return newItemEvent;
    }

    SingleLiveEvent<String> getDialogEvent() {
        return dialogEvent;
    }

    SingleLiveEvent<Collection> getReturnToBuyListEvent() {
        return returnToBuyListEvent;
    }

    SingleLiveEvent<Integer> getSnackbarMessage() {
        return snackbarText;
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
        fabShow.set(true);
    }


    /**
     *  main
    * */

    // onFabClick
    public void addNewItem(long itemId) {
        newItemEvent.setValue(itemId);
    }

    public void saveItem(long collectionId, long itemId) {
        Item item = (itemId == 0 ? new Item() : new Item(itemId));
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем поля
            clearFields();
            layoutFieldsShow.set(false);
            fabShow.set(true);
            snackbarText.setValue(R.string.item_name_is_empty);
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get() + " ");
        item.setUnit(unit.get());

        // новый товар добавляем в базу, редактируемый - обновляем
        if (itemId == 0) {
            repository.addItem(item);
        } else {
            repository.updateItem(item);
            snackbarText.setValue(R.string.item_name_edited);
        }

        // если товар новый - открытие фрагмента для выбора категории
        if (isNewItem(item)) {
            newItemEvent.setValue(item.getId());
        }

        layoutFieldsShow.set(false);
        fabShow.set(true);
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
        if (items.isEmpty()) {
            fabShow.set(true);
        }
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
        fabShow.set(false);
        itemName.set(item.getName());
        quantity.set(item.getQuantity());
        unit.set(item.getUnit());
    }

    private void openDialog() {
        dialogEvent.setValue(CollectionType.BuyList);
        btnToMoveShow.set(false);
    }

    public void transfer(List<Item> items) {
        storage.saveSelectedItems(items);

        long collectionId = storage.loadSelectedCollection();
        if (collectionId == 0) {
            openDialog();
            return;
        }

        transferTo(collectionId, items);
    }

    public void transferTo(long buyListId, List<Item> items) {
        for (int i = 0; i < items.size(); i++) {
            Item item = new Item(i, buyListId, items.get(i).getName(), items.get(i).getCategory(),
                    items.get(i).getCategoryColor(), items.get(i).getQuantity(),
                    items.get(i).getUnit());
            repository.addItem(item);
        }

        btnToMoveShow.set(false);
        snackbarText.setValue(R.string.items_moved);
        openBuyList();
    }

    private void openBuyList() {
        List<Collection> collections = storage.loadCollection(CollectionType.BuyList);
        for (Collection collection : collections) {
            if (collection.getId() == storage.loadSelectedCollection()) {
                returnToBuyListEvent.setValue(collection);
                deleteSelected();
                return;
            }
        }
    }

    public List<Item> loadSelectedItems() {
        return storage.loadSelectedItems();
    }

    public void deleteSelected() {
        storage.deleteSelectedObjects();
    }
}
