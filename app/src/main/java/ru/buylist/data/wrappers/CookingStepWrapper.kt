package ru.buylist.data.wrappers

import ru.buylist.data.entity.CookingStep

data class CookingStepWrapper(
        var step: CookingStep,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)