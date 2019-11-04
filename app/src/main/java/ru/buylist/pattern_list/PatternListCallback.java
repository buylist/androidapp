package ru.buylist.pattern_list;

import java.util.List;

import ru.buylist.data.entity.Item;

public interface PatternListCallback {

    public interface PatternListItemCallback {

        void onItemClick(Item item);

        void onDeleteButtonClick(Item item);

        void onEditButtonClick(Item item);
    }

    void onToMoveButtonClick(List<Item> items);
}
