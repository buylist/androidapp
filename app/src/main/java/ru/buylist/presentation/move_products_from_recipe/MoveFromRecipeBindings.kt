package ru.buylist.presentation.move_products_from_recipe

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.Recipe

object MoveFromRecipeBindings {

    @BindingAdapter("app:recipes")
    @JvmStatic
    fun setRecipes(recycler: RecyclerView, recipes: List<Recipe>?) {
        recipes?.let {
            (recycler.adapter as MoveFromRecipeAdapter).submitList(recipes)
        }
    }
}