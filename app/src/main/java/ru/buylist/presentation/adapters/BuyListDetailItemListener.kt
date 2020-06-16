package ru.buylist.presentation.adapters

import ru.buylist.data.entity.ItemWrapper

interface BuyListDetailItemListener {

    fun onItemClicked(itemWrapper: ItemWrapper)

    fun onButtonMoreClick(itemWrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}