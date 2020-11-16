package ru.buylist.presentation

import android.annotation.SuppressLint
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.TextViewBindingAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.buylist.utils.showKeyboard

object DefaultBindings {

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