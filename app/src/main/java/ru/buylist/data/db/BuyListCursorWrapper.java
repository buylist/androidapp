package ru.buylist.data.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ru.buylist.data.BuyList;
import ru.buylist.data.Category;
import ru.buylist.data.Product;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class BuyListCursorWrapper extends CursorWrapper {

    public BuyListCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public BuyList getBuyList() {
        String uuid = getString(getColumnIndex(BuyTable.Cols.UUID));
        String title = getString(getColumnIndex(BuyTable.Cols.TITLE));

        BuyList buyList = new BuyList(UUID.fromString(uuid));
        buyList.setTitle(title);
        return buyList;
    }

    public Product getProduct() {
        String buylistId = getString(getColumnIndex(ProductTable.Cols.BUYLIST_ID));
        String productId = getString(getColumnIndex(ProductTable.Cols.PRODUCT_ID));
        String productName = getString(getColumnIndex(ProductTable.Cols.PRODUCT_NAME));
        int isPurchased = getInt(getColumnIndex(ProductTable.Cols.IS_PURCHASED));
        String category = getString(getColumnIndex(ProductTable.Cols.CATEGORY));
        String amount = getString(getColumnIndex(ProductTable.Cols.AMOUNT));
        String unit = getString(getColumnIndex(ProductTable.Cols.UNIT));

        Product product = new Product(UUID.fromString(productId));
        product.setBuylistId(buylistId);
        product.setName(productName);
        product.setPurchased(isPurchased != 0);
        product.setCategory(category);
        product.setAmount(amount);
        product.setUnit(unit);
        return product;
    }

    public Product getGlobalProduct() {
        String productId = getString(getColumnIndex(GlobalProductsTable.Cols.PRODUCT_ID));
        String productName = getString(getColumnIndex(GlobalProductsTable.Cols.PRODUCT_NAME));
        String category = getString(getColumnIndex(GlobalProductsTable.Cols.CATEGORY));

        Product product = new Product(UUID.fromString(productId));
        product.setName(productName);
        product.setCategory(category);
        return product;
    }

    public Category getCategory() {
        String id = getString(getColumnIndex(CategoryTable.Cols.CATEGORY_ID));
        String name = getString(getColumnIndex(CategoryTable.Cols.CATEGORY_NAME));
        String color = getString(getColumnIndex(CategoryTable.Cols.CATEGORY_COLOR));

        Category category = new Category(UUID.fromString(id));
        category.setName(name);
        category.setColor(color);
        return category;
    }
}
