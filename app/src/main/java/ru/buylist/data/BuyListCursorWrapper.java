package ru.buylist.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import ru.buylist.models.BuyList;
import ru.buylist.models.Product;

import static ru.buylist.data.BuyListDbSchema.*;

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

    public Product getProductList() {
        String buylistId = getString(getColumnIndex(ProductTable.Cols.BUYLIST_ID));
        String productName = getString(getColumnIndex(ProductTable.Cols.PRODUCT_NAME));
        int isPurchased = getInt(getColumnIndex(ProductTable.Cols.IS_PURCHASED));
        String category = getString(getColumnIndex(ProductTable.Cols.CATEGORY));
        String amount = getString(getColumnIndex(ProductTable.Cols.AMOUNT));
        String unit = getString(getColumnIndex(ProductTable.Cols.UNIT));

        Product product = new Product();
        product.setBuylistId(buylistId);
        product.setName(productName);
        product.setPurchased(isPurchased != 0);
        product.setCategory(category);
        product.setAmount(amount);
        product.setUnit(unit);
        return product;
    }
}
