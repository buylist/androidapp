package ru.buylist.buy_list;

import ru.buylist.data.entity.Item;

public interface BuyListCallback {
    void onItemClick(Item item);

    void onDeleteButtonClick(Item item);

    void onEditButtonClick(Item item);
}
