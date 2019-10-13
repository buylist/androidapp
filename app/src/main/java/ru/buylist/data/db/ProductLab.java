package ru.buylist.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.buylist.data.BuyList;
import ru.buylist.data.Category;
import ru.buylist.data.Pattern;
import ru.buylist.data.Product;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class ProductLab {
    private static ProductLab productLab;
    private Context context;
    private SQLiteDatabase database;

    private ProductLab(Context c) {
        context = c.getApplicationContext();
        database = new BuyListBaseHelper(context).getWritableDatabase();
    }

    public static ProductLab get(Context context) {
        if (productLab == null) {
            productLab = new ProductLab(context);
        }
        return productLab;
    }

    public void addBuyList(BuyList buyList) {
        ContentValues values = BuyListContentValues.getBuyListContentValues(buyList);
        database.insert(BuyTable.NAME, null, values);
    }

    public void addProducts(Product product) {
        ContentValues values = BuyListContentValues.getProductsContentValues(product);
        database.insert(ProductTable.NAME, null, values);
    }

    public void addCategory(Category category) {
        ContentValues values = BuyListContentValues.getCategoryContentValues(category);
        database.insert(CategoryTable.NAME, null, values);
    }

    public void addPattern(Pattern pattern) {
        ContentValues values = BuyListContentValues.getPatternContentValues(pattern);
        database.insert(PatternTable.NAME, null, values);
    }

    public void addGlobalProduct(Product product) {
        ContentValues values = BuyListContentValues.getGlobalProductContentValues(product);
        database.insert(GlobalProductsTable.NAME, null, values);
    }

    public void deleteFromDb(String id, String tableName, String tableCols) {
        database.delete(tableName, tableCols + "=?", new String[]{id});
    }

    // получение списка всех созданных пользователей списков (коллекция списков)
    public List<BuyList> getBuyLists() {
        List<BuyList> lists = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(null, null, BuyTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                lists.add(cursor.getBuyList());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return lists;
    }

    // получения списка по id (из коллекции списков)
    public BuyList getBuyList(UUID id) {
        BuyListCursorWrapper cursor = queryList(
                BuyTable.Cols.UUID + " = ?", new String[]{id.toString()}, BuyTable.NAME);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getBuyList();

        } finally {
            cursor.close();
        }
    }

    // получение всех продуктов для конкретного списка из активной таблицы
    public List<Product> getProductList(String buylistId) {
        List<Product> products = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(
                ProductTable.Cols.BUYLIST_ID + " = ?", new String[]{buylistId}, ProductTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                products.add(cursor.getProduct());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return products;
    }

    // получение конкретного продукта по заданным параметрам из активной таблицы
    public Product getProduct(String values, String tableCols, String tableName) {
        Product product = new Product();
        BuyListCursorWrapper cursor = queryList(
                tableCols + " = ?", new String[]{values}, tableName);
        try {
            cursor.moveToFirst();
            product = cursor.getProduct();
        } finally {
            cursor.close();
        }
        return product;
    }

    // получение конкретной категории по заданным параметрам
    public Category getCategory(String values, String tableCols, String tableName) {
        Category category = new Category();
        BuyListCursorWrapper cursor = queryList(
                tableCols + " = ?", new String[]{values}, tableName);
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                category = cursor.getCategory();
            }
        } finally {
            cursor.close();
        }
        return category;
    }

    // получение списка шаблонов
    public List<Pattern> getPatterns() {
        List<Pattern> patterns = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(null, null, PatternTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                patterns.add(cursor.getPattern());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return patterns;
    }

    // получение всех существующих категорий
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        BuyListCursorWrapper cursor = queryList(
                null, null, CategoryTable.NAME);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                categories.add(cursor.getCategory());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return categories;
    }

    // получение конкретного продукта из глобальной таблицы
    public Product getGlobalProduct(String values, String tableCols, String tableName) {
        Product product = new Product();
        BuyListCursorWrapper cursor = queryList(
                tableCols + " = ?", new String[]{values}, tableName);
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {

                product = cursor.getGlobalProduct();
            }
        } finally {
            cursor.close();
        }
        return product;
    }

    // обновление названия конкретного списка
    public void updateBuyList(BuyList buyList) {
        String buylistId = buyList.getId().toString();
        ContentValues values = BuyListContentValues.getBuyListContentValues(buyList);

        database.update(BuyTable.NAME, values, BuyTable.Cols.UUID + " = ?",
                new String[]{buylistId});
    }

    // полное обновление таблицы с именами списков
    public void updateBuyTable(List<BuyList> lists) {
        database.delete(BuyTable.NAME, null, null);

        for (BuyList buyList : lists) {
            addBuyList(buyList);
        }
    }

    // обновление продукта по id
    public void updateProduct(Product product) {
        ContentValues values = BuyListContentValues.getProductsContentValues(product);
        database.update(ProductTable.NAME, values, ProductTable.Cols.PRODUCT_ID + " = ?",
                new String[]{product.getProductId().toString()});
    }

    // полное обновление таблицы с активными продуктами
    public void updateProductTable(List<Product> products, String id) {
        database.delete(ProductTable.NAME, ProductTable.Cols.BUYLIST_ID + " = ?", new String[]{id});

        for (Product product : products) {
            addProducts(product);
        }
    }

    // обновление названия шаблона
    public void updatePattern(Pattern pattern) {
        ContentValues values = BuyListContentValues.getPatternContentValues(pattern);
        database.update(PatternTable.NAME, values, PatternTable.Cols.ID + " = ?",
                new String[(int) pattern.getId()]);
    }

    // запрос
    private BuyListCursorWrapper queryList(String whereClause, String[] whereArgs, String tableName) {
        Cursor cursor = database.query(
                tableName,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new BuyListCursorWrapper(cursor);
    }
}
