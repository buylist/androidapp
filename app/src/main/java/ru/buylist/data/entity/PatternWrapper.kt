package ru.buylist.data.entity

data class PatternWrapper(
        var pattern: Pattern,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)