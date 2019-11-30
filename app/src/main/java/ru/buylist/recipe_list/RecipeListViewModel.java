package ru.buylist.recipe_list;

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
import ru.buylist.data.DataRepository;
import ru.buylist.data.TemporaryDataStorage;
import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.GlobalItem;
import ru.buylist.data.entity.Item;
import ru.buylist.utils.BuylistApp;
import ru.buylist.utils.SingleLiveEvent;


public class RecipeListViewModel extends AndroidViewModel {

    // ингридиенты
    public final ObservableList<Item> items = new ObservableArrayList<>();

    // Флаги для отображения/скрытия элементов разметки
    public final ObservableBoolean btnToMoveShow = new ObservableBoolean(false);
    public final ObservableBoolean layoutFieldsShow = new ObservableBoolean(false);
    public final ObservableBoolean fieldInstructionShow = new ObservableBoolean(false);

    // Поля для ввода нового ингредиента
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    //Поле инструкции
    public final ObservableField<String> instruction = new ObservableField<>("");

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> newCategoryEvent = new SingleLiveEvent<>();

    // Открытие диалогового окна
    private SingleLiveEvent<String> dialogEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Collection> returnToBuyListEvent = new SingleLiveEvent<>();

    private SingleLiveEvent<Integer> snackbarText = new SingleLiveEvent<>();

    private DataRepository repository;
    private TemporaryDataStorage storage;



    public RecipeListViewModel(Application context) {
        super(context);
        repository = ((BuylistApp) context.getApplicationContext()).getRepository();
        storage = ((BuylistApp) context.getApplicationContext()).getStorage();
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

    public SingleLiveEvent<Collection> getReturnToBuyListEvent() {
        return returnToBuyListEvent;
    }

    public SingleLiveEvent<Integer> getSnackbarMessage() {
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
    }


    /**
     *  main
    * */

    // отображение полей для ввода товара
    public void addNewItem() {
        layoutFieldsShow.set(true);
    }

    // новый товар добавляет в базу, существующий - обновляет
    public void saveItem(long collectionId, long itemId) {
        // id != 0 при редактировании товара, создавать новый не требуется
        Item item = (itemId == 0 ? new Item() : new Item(itemId));
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, обнуляем и скрываем layout
            clearFields();
            layoutFieldsShow.set(false);
            snackbarText.setValue(R.string.item_name_is_empty);
            return;
        }

        item.setCollectionId(collectionId);
        item.setQuantity(quantity.get() + " ");
        item.setUnit(unit.get());

        // новый товар добавляется в базу, редактируемый - обновляется
        if (itemId == 0) {
            repository.addItem(item);
        } else {
            repository.updateItem(item);
            snackbarText.setValue(R.string.item_name_edited);
        }

        // если товар новый - открывается CategoryFragment для выбора категории
        if (isNewItem(item)) {
            newCategoryEvent.setValue(item.getId());
        }

        layoutFieldsShow.set(false);
        clearFields();
    }

    public void saveInstruction(Collection collection) {
        collection.setDescription(instruction.get());
        fieldInstructionShow.set(false);
        repository.updateCollection(collection);
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

    public void loadInstruction(String instruction) {
        this.instruction.set(instruction);
    }

    private void clearFields() {
        itemName.set("");
        quantity.set("");
        unit.set("");
    }

    // отображение полей для редактирования товара
    public void editItem(Item item) {
        layoutFieldsShow.set(true);
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
