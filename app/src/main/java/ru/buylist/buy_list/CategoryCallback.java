package ru.buylist.buy_list;

import ru.buylist.data.entity.Category;

public interface CategoryCallback {
    interface CircleCallback {
        void onCircleClick(Category category);
    }

    void onCreateButtonClick();

    void onPrevCircleButtonClick();

    void onNextCircleButtonClick();
}
