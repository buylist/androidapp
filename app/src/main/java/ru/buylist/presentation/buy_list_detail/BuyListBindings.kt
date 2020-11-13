package ru.buylist.presentation.buy_list_detail

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.wrappers.ItemWrapper

object BuyListBindings {
    @BindingAdapter("app:buyListProducts")
    @JvmStatic
    fun setBuyListProducts(recycler: RecyclerView, products: List<ItemWrapper>?) {
        products?.let {
            (recycler.adapter as BuyListDetailAdapter).submitList(products)
        }
    }

    @BindingAdapter("app:imeActionDoneListener")
    @JvmStatic
    fun setEditorActionListener(field: EditText, viewModel: BuyListDetailViewModel) {
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