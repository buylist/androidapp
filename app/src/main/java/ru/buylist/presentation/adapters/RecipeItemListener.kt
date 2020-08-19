package ru.buylist.presentation.adapters

import ru.buylist.data.entity.Recipe

interface RecipeItemListener {

    fun onRecipeClicked(recipe: Recipe)

    fun onButtonMoreClick(recipe: Recipe)
}