package ru.buylist.data.entity


data class ItemWrapper(
        var item: Item,
        var globalPosition: Int,
        var localPosition: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)