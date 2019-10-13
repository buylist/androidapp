package ru.buylist.data.db;

import android.content.ContentValues;

import ru.buylist.data.BuyList;
import ru.buylist.data.Category;
import ru.buylist.data.Pattern;
import ru.buylist.data.Product;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class BuyListContentValues {

    protected static ContentValues getBuyListContentValues(BuyList buyList) {
        ContentValues values = new ContentValues();
        values.put(BuyTable.Cols.UUID, String.valueOf(buyList.getId()));
        values.put(BuyTable.Cols.TITLE, buyList.getTitle());
        return values;
    }

    protected static ContentValues getProductsContentValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(ProductTable.Cols.BUYLIST_ID, String.valueOf(product.getBuylistId()));
        values.put(ProductTable.Cols.PRODUCT_ID, String.valueOf(product.getProductId()));
        values.put(ProductTable.Cols.PRODUCT_NAME, product.getName());
        values.put(ProductTable.Cols.IS_PURCHASED, product.isPurchased() ? 1 : 0);
        values.put(ProductTable.Cols.CATEGORY, product.getCategory());
        values.put(ProductTable.Cols.AMOUNT, product.getAmount());
        values.put(ProductTable.Cols.UNIT, product.getUnit());
        return values;
    }

    protected static ContentValues getCategoryContentValues(Category category) {
        ContentValues values = new ContentValues();
        values.put(CategoryTable.Cols.CATEGORY_ID, String.valueOf(category.getId()));
        values.put(CategoryTable.Cols.CATEGORY_NAME, category.getName());
        values.put(CategoryTable.Cols.CATEGORY_COLOR, category.getColor());
        return values;
    }

    protected static ContentValues getPatternContentValues(Pattern pattern) {
        ContentValues values = new ContentValues();
        values.put(PatternTable.Cols.ID, String.valueOf(pattern.getId()));
        values.put(PatternTable.Cols.TITLE, pattern.getName());
        return values;
    }

    protected static ContentValues getGlobalProductContentValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(GlobalProductsTable.Cols.PRODUCT_ID, String.valueOf(product.getProductId()));
        values.put(GlobalProductsTable.Cols.PRODUCT_NAME, product.getName());
        values.put(GlobalProductsTable.Cols.CATEGORY, product.getCategory());
        return values;
    }
}
