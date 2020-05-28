package ru.buylist.buy_list;


public interface CategoryCallback {
    interface CircleCallback {
        void onCircleClick(Category category);
    }

    void onCreateButtonClick();

    void onPrevCircleButtonClick();

    void onNextCircleButtonClick();
}
