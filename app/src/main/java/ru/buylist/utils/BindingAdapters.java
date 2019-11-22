package ru.buylist.utils;

import androidx.databinding.BindingAdapter;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    @BindingAdapter("app:buyItems")
    public static void setItems(RecyclerView recyclerView, List<Item> items) {
        BuyListAdapter adapter = (BuyListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    // устанавливает список товаров в шаблоне
    @BindingAdapter("app:patternItems")
    public static void setPatternItems(RecyclerView recyclerView, List<Item> items) {
        PatternListAdapter adapter = (PatternListAdapter) recyclerView.getAdapter();
        if (adapter != null) {
            adapter.setItems(items);
        }
    }

    // Скрытие / отображение fab с анимацией
    @BindingAdapter("fabVisibility")
    public static void setFabVisibility(FloatingActionButton button, boolean isShown) {
        if (!isShown) {
            button.hide();
        } else {
            button.show();
        }
    }

    @BindingAdapter("requestFocus")
    public static void setFocus(EditText field, boolean isFocusable) {
        if (isFocusable) {
            field.requestFocus();
        } else {
            field.clearFocus();
        }
    }
}
