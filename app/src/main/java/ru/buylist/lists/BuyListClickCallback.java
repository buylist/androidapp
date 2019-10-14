package ru.buylist.lists;

import ru.buylist.data.BuyList;

public interface BuyListClickCallback {
    void onListItemClick(BuyList buyList);

    void onDeleteButtonClick(long itemId);

    void onEditButtonClick(BuyList buyList);
}
