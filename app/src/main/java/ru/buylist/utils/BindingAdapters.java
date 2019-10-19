package ru.buylist.utils;

import android.databinding.BindingAdapter;
import android.view.View;


public class BindingAdapters {

    //  задает видимость слою создания нового листа
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // задает видимость recyclerView
    @BindingAdapter("recyclerVisible")
    public static void recyclerLoad(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
