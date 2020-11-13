package ru.buylist.data.wrappers

import ru.buylist.data.entity.Pattern

data class PatternWrapper(
        var pattern: Pattern,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)