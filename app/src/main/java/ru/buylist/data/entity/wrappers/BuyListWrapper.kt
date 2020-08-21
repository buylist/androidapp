package ru.buylist.data.entity.wrappers

import ru.buylist.data.entity.BuyList

data class BuyListWrapper(
        var buyList: BuyList,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)