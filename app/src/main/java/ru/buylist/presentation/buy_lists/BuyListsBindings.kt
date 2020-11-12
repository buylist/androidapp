package ru.buylist.presentation.buy_lists

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.wrappers.BuyListWrapper

object BuyListsBindings {

    @BindingAdapter("app:buyLists")
    @JvmStatic
    fun setBuyLists(recycler: RecyclerView, buyLists: List<BuyListWrapper>?) {
        buyLists?.let {
            (recycler.adapter as BuyListsAdapter).submitList(buyLists)
        }
    }

    @BindingAdapter("app:imeActionDoneListener")
    @JvmStatic
    fun setEditorActionListener(field: EditText, viewModel: BuyListsViewModel) {
        field.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.save()
                    true
                }
                else -> false
            }
        }
    }

    @BindingAdapter("app:onScrolled")
    @JvmStatic
    fun setScrollListener(recycler: RecyclerView, viewModel: BuyListsViewModel) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }
}