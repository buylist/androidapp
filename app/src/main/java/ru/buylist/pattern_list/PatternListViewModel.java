package ru.buylist.pattern_list;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import ru.buylist.utils.BuylistApp;
import ru.buylist.data.DataRepository;
import ru.buylist.utils.SingleLiveEvent;
import ru.buylist.data.entity.GlobalItem;
import ru.buylist.data.entity.Item;


public class PatternListViewModel extends AndroidViewModel {

    // Флаги для отображения/скрытия элементов разметки
    public final ObservableBoolean buttonMoveVisibility = new ObservableBoolean(false);
    public final ObservableBoolean layoutNewProductVisibility = new ObservableBoolean(false);

    // Поля для ввода нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> itemCreated;

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    private SingleLiveEvent<Long> categoryAdded;

    private DataRepository repository;

    public PatternListViewModel(Application context) {
        super(context);
        repository = ((BuylistApp) context.getApplicationContext()).getRepository();
    }

    SingleLiveEvent<Long> getProductCreated() {
        return itemCreated;
    }

    SingleLiveEvent<Long> getCategoryAdded() {
        return categoryAdded;
    }

    // onFabClick
    public void addNewItem() {
        layoutNewProductVisibility.set(true);
    }

    public void saveItem() {
        Item item = new Item();
        item.setName(itemName.get());
        if (item.isEmpty()) {
            // товар не может быть пустым, ничего не делаем
            return;
        }
        if (isNewItem(item)) {
            item.setQuantity(quantity.get());
            item.setUnit(unit.get());
            createItem(item);
        } else {
            updateItem(item);
        }
    }

    public void savecategory(long productId) {

    }

    // проверка на наличие в GlobalProductTable
    private boolean isNewItem(Item item) {
        GlobalItem globalItem = repository.getGlobalItem(item.getName());
        if (globalItem == null || globalItem.getName() == null) {
            return true;
        } else {
            item.setCategory(item.getCategory());
            return false;
        }
    }

    // добавление в базу и т.к. товар новый - вызов event для открытия фрагмента с выбором категории
    private void createItem(Item item) {
        repository.updateItem(item);
        itemCreated.call();
    }

    // обновление товара в базе
    private void updateItem(Item item) {
        repository.updateItem(item);
    }


}
