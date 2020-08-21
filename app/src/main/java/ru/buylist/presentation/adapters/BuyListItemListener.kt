package ru.buylist.presentation.adapters

import ru.buylist.data.entity.wrappers.BuyListWrapper

interface BuyListItemListener {

    fun onBuyListClicked(buyListWrapper: BuyListWrapper)

    fun onButtonMoreClick(buyListWrapper: BuyListWrapper)

    fun onButtonSaveClick(buyListWrapper: BuyListWrapper)
}