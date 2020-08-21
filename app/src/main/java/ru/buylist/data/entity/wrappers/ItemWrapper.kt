package ru.buylist.data.entity.wrappers

import ru.buylist.data.entity.Item


data class ItemWrapper(
        var item: Item,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)