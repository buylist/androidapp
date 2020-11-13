package ru.buylist.presentation.recipes

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.wrappers.RecipeWrapper

object RecipesBindings {

    @BindingAdapter("app:recipes")
    @JvmStatic
    fun setRecipes(recycler: RecyclerView, recipes: List<RecipeWrapper>?) {
        recipes?.let {
            (recycler.adapter as RecipesAdapter).submitList(recipes)
        }
    }
}