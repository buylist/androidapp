package ru.buylist.listpattern;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import ru.buylist.SingleLiveEvent;
import ru.buylist.data.Product;
import ru.buylist.data.db.ProductLab;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class ListPatternViewModel extends AndroidViewModel {

    // Флаги для отображения/скрытия элементов разметки
    public final ObservableBoolean buttonMoveVisibility = new ObservableBoolean(false);
    public final ObservableBoolean layoutNewProductVisibility = new ObservableBoolean(false);

    // Поля для ввода нового товара
    public final ObservableField<String> itemName = new ObservableField<>();
    public final ObservableField<String> quantity = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<Long> productCreated;

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    private SingleLiveEvent<Long> categoryAdded;

    private final Context context;
    private ProductLab productLab;

    public ListPatternViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        productLab = ProductLab.get(context);
    }

    SingleLiveEvent<Long> getProductCreated() {
        return productCreated;
    }

    SingleLiveEvent<Long> getCategoryAdded() {
        return categoryAdded;
    }

    // onFabClick
    public void addNewProduct() {
        buttonMoveVisibility.set(true);
        layoutNewProductVisibility.set(true);
    }

    public void saveProduct() {
        Product product = new Product();
        product.setName(itemName.get());
        product.setAmount(quantity.get());
        product.setUnit(unit.get());

        if (product.isEmpty()) {
            // товар не может быть пустым, ничего не делаем
            return;
        }
        if (isNewProduct(product)) {
            createProduct(product);
        } else {
            updateProduct(product);
        }
    }

    public void savecategory(long productId) {

    }

    // проверка на наличие в GlobalProductTable
    private boolean isNewProduct(Product product) {
        Product globalProduct = productLab.getGlobalProduct(
                product.getName(),
                GlobalProductsTable.Cols.PRODUCT_NAME,
                GlobalProductsTable.NAME);
        if (globalProduct.getName() == null) {
            return true;
        } else {
            product.setCategory(globalProduct.getCategory());
            return false;
        }
    }

    // добавление в базу и т.к. товар новый - вызов event для открытия фрагмента с выбором категории
    private void createProduct(Product product) {
        productLab.updateProduct(product);
        productCreated.call();
    }

    // обновление товара в базе
    private void updateProduct(Product product) {
        productLab.updateProduct(product);
    }


}
