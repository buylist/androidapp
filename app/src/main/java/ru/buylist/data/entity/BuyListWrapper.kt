package ru.buylist.data.entity

data class BuyListWrapper(
        var buyList: BuyList,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)