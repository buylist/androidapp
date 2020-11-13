package ru.buylist.data.wrappers

import ru.buylist.data.entity.Recipe

data class RecipeWrapper(
        var recipe: Recipe,
        var position: Int,
        var isEditable: Boolean = false,
        var isSelected: Boolean = false
)