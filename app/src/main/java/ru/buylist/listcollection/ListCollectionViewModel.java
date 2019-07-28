package ru.buylist.listcollection;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;
import java.util.UUID;

import ru.buylist.KeyboardUtils;
import ru.buylist.SingleLiveEvent;
import ru.buylist.data.BuyList;
import ru.buylist.data.Category;
import ru.buylist.data.Product;
import ru.buylist.data.ProductLab;

import static ru.buylist.data.db.BuyListDbSchema.*;


public class ListCollectionViewModel extends AndroidViewModel {


    public final ObservableList<Product> products = new ObservableArrayList<>();

    // Флаги для отображения/скрытия элементов
    public final ObservableBoolean layoutNewProductVisibility = new ObservableBoolean(false);
    public final ObservableBoolean fabNewProductVisibility = new ObservableBoolean(true);
    public final ObservableBoolean fabProductsVisibility = new ObservableBoolean(true);
    public final ObservableBoolean bottomNavigationVisibility = new ObservableBoolean(true);
    public final ObservableBoolean purchasedProductsVisibility = new ObservableBoolean(true);

    // Поля ввода данных нового товара
    public final ObservableField<String> productName = new ObservableField<>();
    public final ObservableField<String> amount = new ObservableField<>("");
    public final ObservableField<String> unit = new ObservableField<>("");

    private final Context context;

    // true = Action.ADD, false = Action.UPDATE
    public boolean createButtonFlag = true;

    private ProductLab productLab;

    // Отслеживание нового товара для открытия CategoryFragment
    private SingleLiveEvent<String> newCategoryEvent = new SingleLiveEvent<>();

    // Отслеживает нажатие на кнопки "Далее" и "Пропустить" в CategoryFragment для перехода в список
    private SingleLiveEvent<String> addProductEvent = new SingleLiveEvent<>();

    public ListCollectionViewModel(Application context) {
        super(context);
        this.context = context.getApplicationContext();
        productLab = ProductLab.get(context);
    }

    SingleLiveEvent<String> getNewCategoryEvent() {
        return newCategoryEvent;
    }

    SingleLiveEvent<String> getAddProductEvent() {
        return addProductEvent;
    }

    public BuyList getBuyList(UUID buylistId) {
        return productLab.getBuyList(buylistId);
    }

    public Product getProduct(String productId) {
        return productLab.getProduct(productId,
                ProductTable.Cols.PRODUCT_ID,
                ProductTable.NAME);
    }

    public List<Product> getProducts(UUID id) {
        List<Product> productsToShow = productLab.getProductList(id.toString());
        products.clear();
        products.addAll(productsToShow);
        return products;
    }

    public void updateBuyList(BuyList buyList) {
        productLab.updateBuyList(buyList);
    }

    public void showNewProductLayout(EditText targetField) {
        createButtonFlag = true;
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

    public void showAllProducts() {
        if (purchasedProductsVisibility.get()) {
            purchasedProductsVisibility.set(false);
        } else {
            purchasedProductsVisibility.set(true);
        }
    }

    //скрытие и отображение купленных продуктов при нажатии на fab
    public void setViewVisibility(View itemView, Product product) {
        if (product.isPurchased()) {
            if (purchasedProductsVisibility.get()) {
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            } else {
                itemView.setVisibility(View.VISIBLE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        } else {
            itemView.setVisibility(View.VISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public void saveProduct(EditText targetField, String buylistId, UUID productId) {
        if (!productName.get().isEmpty()) {
            Product product = createProduct(buylistId, productId);
            if (!isInGlobalDatabase(product)) {
                newCategoryEvent.setValue(product.getProductId().toString());
            }

            makeAction(createButtonFlag ? Action.ADD : Action.UPDATE, product);
        }
        clearFields();
        hideNewProductLayout(targetField);
    }

    private Product createProduct(String buylistId, UUID productId) {
        Product product = (productId == null ? new Product() : new Product(productId));
        product.setBuylistId(buylistId);
        product.setName(productName.get());
        product.setAmount(amount.get());
        product.setUnit(unit.get());
        return product;
    }

    private void clearFields() {
        productName.set("");
        amount.set("");
        unit.set("");
    }

    //проверка на наличие товара в глобальной базе
    private boolean isInGlobalDatabase(Product product) {
        Product globalProduct = productLab.getGlobalProduct(product.getName(),
                GlobalProductsTable.Cols.PRODUCT_NAME,
                GlobalProductsTable.NAME);
        if (globalProduct.getName() == null) {
            return false;
        } else {
            product.setCategory(globalProduct.getCategory());
            return true;
        }
    }

    public int getColor(Product product) {
        Product globalProduct = productLab.getGlobalProduct(
                product.getName(),
                GlobalProductsTable.Cols.PRODUCT_NAME,
                GlobalProductsTable.NAME);
        if (globalProduct.getCategory() == null) {
            return Color.BLACK;
        } else {
            Category category = productLab.getCategory(
                    globalProduct.getCategory(),
                    CategoryTable.Cols.CATEGORY_NAME,
                    CategoryTable.NAME);
            return Color.parseColor(category.getColor());
        }
    }

    public void checkProduct(Product product) {
        if (product.isPurchased()) {
            product.setPurchased(false);
        } else {
            product.setPurchased(true);
        }
        makeAction(Action.UPDATE, product);
    }

    public void makeAction(Action action, Product product) {
        switch (action) {
            case ADD:
                productLab.addProducts(product);
                break;
            case UPDATE:
                productLab.updateProduct(product);
                break;
            case DELETE:
                productLab.deleteFromDb(product.getProductId().toString(),
                        ProductTable.NAME,
                        ProductTable.Cols.PRODUCT_ID);
                break;
            default:
                break;
        }
    }

    public void editProduct(Product product) {
        layoutNewProductVisibility.set(true);
        createButtonFlag = false;
        productName.set(product.getName());
        amount.set(product.getAmount());
        unit.set(product.getUnit());
    }

    public void updateCategory(String categoryName, Product product) {
        Category category = productLab.getCategory(
                categoryName,
                CategoryTable.Cols.CATEGORY_NAME,
                CategoryTable.NAME);

        product.setCategory(category.getName());
        productLab.updateProduct(product);
        productLab.addGlobalProduct(product);
        addProductEvent.setValue(product.getBuylistId());
    }

    public void skipCategory(String buylistId) {
        addProductEvent.setValue(buylistId);
    }

}
