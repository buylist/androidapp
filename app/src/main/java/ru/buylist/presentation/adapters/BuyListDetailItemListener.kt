package ru.buylist.presentation.adapters

import ru.buylist.data.entity.wrappers.ItemWrapper

interface BuyListDetailItemListener {

    fun onItemClicked(itemWrapper: ItemWrapper)

    fun onButtonMoreClick(itemWrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}