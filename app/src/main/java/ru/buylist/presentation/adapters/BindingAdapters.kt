package ru.buylist.presentation.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.Pattern
import ru.buylist.data.entity.Recipe

object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic fun setItems(recycler: RecyclerView, items: List<BuyList>) {
        with(recycler.adapter as BuyListAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setPatterns(recycler: RecyclerView, items: List<Pattern>) {
        with(recycler.adapter as PatternAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setRecipes(recycler: RecyclerView, items: List<Recipe>) {
        with(recycler.adapter as RecipeAdapter) {
            list = items
        }
    }
}