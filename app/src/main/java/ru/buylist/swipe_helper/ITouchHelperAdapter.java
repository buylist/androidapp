package ru.buylist.swipe_helper;

public interface ITouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
