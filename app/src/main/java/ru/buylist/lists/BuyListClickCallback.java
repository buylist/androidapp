package ru.buylist.lists;

import ru.buylist.data.BuyList;

public interface BuyListClickCallback {
    void onListItemClick(BuyList buyList);

    void onDeleteButtonClick(BuyList buyList);

    void onEditButtonClick(BuyList buyList);
}
