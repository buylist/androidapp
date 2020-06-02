package ru.buylist.presentation.adapters

import ru.buylist.data.entity.BuyList

interface BuyListItemListener {

    fun onBuyListClicked(buyList: BuyList)

    fun onButtonMoreClick(buyList: BuyList)
}