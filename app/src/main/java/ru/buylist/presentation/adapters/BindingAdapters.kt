package ru.buylist.presentation.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.buylist.data.entity.wrappers.*
import ru.buylist.presentation.adapters.recipe_adapters.RecipeItemsAdapter
import ru.buylist.presentation.adapters.recipe_adapters.RecipeStepsAdapter

object BindingAdapters {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setBuyLists(recycler: RecyclerView, items: List<BuyListWrapper>) {
        with(recycler.adapter as BuyListAdapter) {
            list = items
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setPatterns(recycler: RecyclerView, patterns: List<PatternWrapper>) {
        with(recycler.adapter as PatternsAdapter) {
            list = patterns
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setRecipes(recycler: RecyclerView, recipes: List<RecipeWrapper>?) {
        recipes?.let {
            (recycler.adapter as RecipesAdapter).submitList(recipes)
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recycler: RecyclerView,
                 items: List<ItemWrapper>) {
        with(recycler.adapter as BuyListDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:patternItems")
    @JvmStatic
    fun setPatternItems(recycler: RecyclerView,
                        items: List<ItemWrapper>) {
        with(recycler.adapter as PatternDetailAdapter) {
            wrappedItems = items
        }
    }

    @BindingAdapter("app:recipeItems")
    @JvmStatic
    fun setRecipeItems(recycler: RecyclerView, items: List<ItemWrapper>) {
        with(recycler.adapter as ConcatAdapter) {
            for (adapter in adapters) {
                if (adapter is RecipeItemsAdapter) {
                    adapter.setData(items)
                }
            }
        }
    }

    @BindingAdapter("app:recipeSteps")
    @JvmStatic
    fun setRecipeSteps(recycler: RecyclerView, steps: List<CookingStepWrapper>) {
        with(recycler.adapter as ConcatAdapter) {
            for (adapter in adapters) {
                if (adapter is RecipeStepsAdapter) {
                    adapter.setData(steps)
                }
            }
        }
    }

    @BindingAdapter("app:items")
    @JvmStatic
    fun setCircles(recycler: RecyclerView, circles: List<CircleWrapper>) {
        with(recycler.adapter as CirclesAdapter) {
            list = circles
        }
    }

    @BindingAdapter("app:fabVisibility")
    @JvmStatic
    fun setFabVisibility(fab: FloatingActionButton, isShown: Boolean) {
        if (isShown) {
            fab.show()
        } else {
            fab.hide()
        }
    }
}