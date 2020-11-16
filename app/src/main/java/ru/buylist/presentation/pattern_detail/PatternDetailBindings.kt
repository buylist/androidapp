package ru.buylist.presentation.pattern_detail

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.wrappers.ItemWrapper

object PatternDetailBindings {

    @BindingAdapter("app:patternProducts")
    @JvmStatic
    fun setPatternItems(recycler: RecyclerView, products: List<ItemWrapper>?) {
        products?.let {
            (recycler.adapter as PatternDetailAdapter).submitList(products)
        }
    }

    @BindingAdapter("app:imeActionDoneListener")
    @JvmStatic
    fun setEditorActionListener(field: EditText, viewModel: PatternDetailViewModel) {
        field.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.saveNewItem()
                    true
                }
                else -> false
            }
        }
    }
}