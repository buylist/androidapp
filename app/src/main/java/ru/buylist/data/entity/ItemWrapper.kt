package ru.buylist.data.entity


data class ItemWrapper(
        var item: Item,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)