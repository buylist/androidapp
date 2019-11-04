package ru.buylist.utils;

import ru.buylist.data.entity.Collection;
import ru.buylist.data.entity.Item;

public interface ItemClickCallback {

    interface ItemCollectionCallback {
        void onListItemClick(Collection collection);

        void onDeleteButtonClick(Collection collection);

        void onEditButtonClick(Collection collection);
    }

    interface ItemCallback {
        void onItemClick(Item item);

        void onDeleteButtonClick(Item item);

        void onEditButtonClick(Item item);
    }
}
