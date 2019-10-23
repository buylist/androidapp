package ru.buylist.collection_lists;

import ru.buylist.data.entity.Collection;

public interface CollectionClickCallback {
    void onListItemClick(Collection collection);

    void onDeleteButtonClick(Collection collection);

    void onEditButtonClick(Collection collection);
}
