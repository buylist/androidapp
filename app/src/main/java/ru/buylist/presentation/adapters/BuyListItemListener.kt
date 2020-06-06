package ru.buylist.presentation.adapters

import ru.buylist.data.entity.BuyListWrapper

interface BuyListItemListener {

    fun onBuyListClicked(buyList: BuyListWrapper)

    fun onButtonMoreClick(buyList: BuyListWrapper)

    fun onButtonSaveClick(buyList: BuyListWrapper)
}