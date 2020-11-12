package ru.buylist.presentation

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.entity.wrappers.PatternWrapper
import ru.buylist.presentation.adapters.BuyListDetailAdapter
import ru.buylist.presentation.adapters.CirclesAdapter
import ru.buylist.presentation.adapters.PatternDetailAdapter
import ru.buylist.presentation.adapters.PatternsAdapter
import ru.buylist.utils.showKeyboard

object DefaultBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setPatterns(recycler: RecyclerView, patterns: List<PatternWrapper>) {
        with(recycler.adapter as PatternsAdapter) {
            list = patterns
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recycler: RecyclerView,
                 items: List<ItemWrapper>) {
        with(recycler.adapter as BuyListDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:patternItems")
    @JvmStatic
    fun setPatternItems(recycler: RecyclerView,
                        items: List<ItemWrapper>) {
        with(recycler.adapter as PatternDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setCircles(recycler: RecyclerView, circles: List<CircleWrapper>) {
        with(recycler.adapter as CirclesAdapter) {
            list = circles
        }
    }

    @BindingAdapter("app:fabVisibility")
    @JvmStatic
    fun setFabVisibility(fab: FloatingActionButton, isShown: Boolean) {
        if (isShown) {
            fab.show()
        } else {
            fab.hide()
        }
    }

    @SuppressLint("RestrictedApi")
    @BindingAdapter("android:text")
    @JvmStatic
    fun setSelection(field: EditText, text: String?) {
        TextViewBindingAdapter.setText(field, text)

        if (text == null || text.isEmpty()) return
        field.setSelection(text.length)
    }

    @BindingAdapter("app:requestFocus")
    @JvmStatic
    fun requestFocus(field: EditText, requestFocus: Boolean) {
        if (requestFocus) {
            field.showKeyboard()
        }
    }
}