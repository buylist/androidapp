package ru.buylist.presentation.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.buylist.data.entity.*

object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic fun setBuyLists(recycler: RecyclerView, items: List<BuyListWrapper>) {
        with(recycler.adapter as BuyListAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setPatterns(recycler: RecyclerView, items: List<Pattern>) {
        with(recycler.adapter as PatternsAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setRecipes(recycler: RecyclerView, items: List<Recipe>) {
        with(recycler.adapter as RecipeAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setItems(recycler: RecyclerView,
                            items: List<ItemWrapper>) {
        with(recycler.adapter as BuyListDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:patternItems")
    @JvmStatic fun setPatternItems(recycler: RecyclerView,
                            items: List<ItemWrapper>) {
        with(recycler.adapter as PatternDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic fun setCircles(recycler: RecyclerView, circles: List<CircleWrapper>) {
        with(recycler.adapter as CirclesAdapter) {
            list = circles
        }
    }

    @BindingAdapter("app:fabVisibility")
    @JvmStatic fun setFabVisibility(fab: FloatingActionButton, isShown: Boolean) {
        if (isShown) {
            fab.show()
        } else {
            fab.hide()
        }
    }
}