package ru.buylist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static ru.buylist.data.BuyListDbSchema.*;

public class BuyListBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "buylistBase.db";

    public BuyListBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder buyTableBuilder = new StringBuilder();
        buyTableBuilder.append("create table ");
        buyTableBuilder.append(BuyTable.NAME);
        buyTableBuilder.append("(");
        buyTableBuilder.append(" _id integer primary key autoincrement, ");
        buyTableBuilder.append(BuyTable.Cols.UUID);
        buyTableBuilder.append(", ");
        buyTableBuilder.append(BuyTable.Cols.TITLE);
        buyTableBuilder.append(")");

        StringBuilder productTableBuilder = new StringBuilder();
        productTableBuilder.append("create table ");
        productTableBuilder.append(ProductTable.NAME);
        productTableBuilder.append("(");
        productTableBuilder.append(" _id integer primary key autoincrement, ");
        productTableBuilder.append(ProductTable.Cols.BUYLIST_ID);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.PRODUCT_ID);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.PRODUCT_NAME);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.IS_PURCHASED);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.CATEGORY);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.AMOUNT);
        productTableBuilder.append(", ");
        productTableBuilder.append(ProductTable.Cols.UNIT);
        productTableBuilder.append(")");

        db.execSQL(buyTableBuilder.toString());
        db.execSQL(productTableBuilder.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
