package ru.buylist.data.entity.wrappers

data class CookingStepWrapper(
        var step: String,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)