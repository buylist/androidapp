package ru.buylist.presentation.buy_lists

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
}