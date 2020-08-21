package ru.buylist.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.buylist.data.entity.wrappers.BuyListWrapper

class BuyListDiffCallback : DiffUtil.ItemCallback<BuyListWrapper>() {
    override fun areItemsTheSame(oldItem: BuyListWrapper, newItem: BuyListWrapper): Boolean {
        return oldItem.buyList.id == newItem.buyList.id
    }

    override fun areContentsTheSame(oldItem: BuyListWrapper, newItem: BuyListWrapper): Boolean {
        return oldItem == newItem
    }
}