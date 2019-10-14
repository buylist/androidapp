package ru.buylist.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.buylist.data.db.BuyListDbSchema.*;

public class BuyListBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "buylistBase.db";

    public BuyListBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создание таблицы с названиями списков (коллекция списков)
        StringBuilder buyTable = new StringBuilder();
        buyTable.append("create table ");
        buyTable.append(BuyTable.NAME);
        buyTable.append("(");
        buyTable.append(BuyTable.Cols.UUID);
        buyTable.append(" primary key, ");
        buyTable.append(BuyTable.Cols.TITLE);
        buyTable.append(")");

        // создание таблицы с продуктами из активных списков
        StringBuilder productTable = new StringBuilder();
        productTable.append("create table ");
        productTable.append(ProductTable.NAME);
        productTable.append("(");
        productTable.append(" _id integer primary key autoincrement, ");
        productTable.append(ProductTable.Cols.BUYLIST_ID);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.PRODUCT_ID);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.PRODUCT_NAME);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.IS_PURCHASED);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.CATEGORY);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.AMOUNT);
        productTable.append(", ");
        productTable.append(ProductTable.Cols.UNIT);
        productTable.append(")");

        // создание таблицы с категориями товаров
        StringBuilder categoryTable = new StringBuilder();
        categoryTable.append("create table ");
        categoryTable.append(CategoryTable.NAME);
        categoryTable.append("(");
        categoryTable.append(" _id integer primary key autoincrement, ");
        categoryTable.append(CategoryTable.Cols.CATEGORY_ID);
        categoryTable.append(", ");
        categoryTable.append(CategoryTable.Cols.CATEGORY_NAME);
        categoryTable.append(", ");
        categoryTable.append(CategoryTable.Cols.CATEGORY_COLOR);
        categoryTable.append(")");

        // создание таблицы с именами шаблонов
        StringBuilder patternTable = new StringBuilder();
        patternTable.append("create table ");
        patternTable.append(PatternTable.NAME);
        patternTable.append("(");
        patternTable.append(PatternTable.Cols.ID);
        patternTable.append(" primary key, ");
        patternTable.append(PatternTable.Cols.TITLE);
        patternTable.append(")");

        // создание глобальной таблицы продуктов
        StringBuilder globalTable = new StringBuilder();
        globalTable.append("create table ");
        globalTable.append(GlobalProductsTable.NAME);
        globalTable.append("(");
        globalTable.append(" _id integer primary key autoincrement, ");
        globalTable.append(GlobalProductsTable.Cols.PRODUCT_ID);
        globalTable.append(", ");
        globalTable.append(GlobalProductsTable.Cols.PRODUCT_NAME);
        globalTable.append(", ");
        globalTable.append(GlobalProductsTable.Cols.CATEGORY);
        globalTable.append(")");


        db.execSQL(buyTable.toString());
        db.execSQL(productTable.toString());
        db.execSQL(categoryTable.toString());
        db.execSQL(patternTable.toString());
        db.execSQL(globalTable.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
