package ru.buylist.utils;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import ru.buylist.buy_list.BuyListAdapter;
import ru.buylist.data.entity.Item;
import ru.buylist.pattern_list.PatternListAdapter;


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

    // устанавливает список товаров
    @BindingAdapter("app:items")
    public static void setItems(RecyclerView recyclerView, List<Item> items) {
        BuyListAdapter adapter = (BuyListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    // устанавливает список товаров в шаблоне
    @BindingAdapter("app:items")
    public static void setPatternItems(RecyclerView recyclerView, List<Item> items) {
        PatternListAdapter adapter = (PatternListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(items);
        }
    }
}
