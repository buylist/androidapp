package ru.buylist.presentation

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.buylist.data.wrappers.ItemWrapper
import ru.buylist.presentation.pattern_detail.PatternDetailAdapter
import ru.buylist.utils.showKeyboard

object DefaultBindings {

    @BindingAdapter("app:patternItems")
    @JvmStatic
    fun setPatternItems(recycler: RecyclerView,
                        items: List<ItemWrapper>) {
        with(recycler.adapter as PatternDetailAdapter) {
            wrappedItems = items
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