package ru.buylist.utils;

import ru.buylist.data.entity.Collection;

public interface ItemClickCallback {

    interface ItemCollectionCallback {
        void onListItemClick(Collection collection);

        void onDeleteButtonClick(Collection collection);

        void onEditButtonClick(Collection collection);
    }
}
