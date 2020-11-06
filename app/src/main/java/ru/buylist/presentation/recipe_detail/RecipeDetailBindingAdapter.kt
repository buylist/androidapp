package ru.buylist.presentation.recipe_detail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item

object RecipeDetailBindingAdapter {

    @BindingAdapter("app:ingredients")
    @JvmStatic
    fun setIngredients(recycler: RecyclerView, items: List<Item>?) {
        with(recycler.adapter as ConcatAdapter) {
            items?.let {
                for (adapter in adapters) {
                    if (adapter is RecipeDetailItemsAdapter) {
                        adapter.submitList(items)
                    }
                }
            }
        }
    }

    @BindingAdapter("app:cookingStep")
    @JvmStatic
    fun setCookingStep(recycler: RecyclerView, steps: List<CookingStep>?) {
        with(recycler.adapter as ConcatAdapter) {
            steps?.let {
                for (adapter in adapters) {
                    if (adapter is RecipeDetailStepsAdapter) {
                        adapter.submitList(steps)
                    }
                }
            }
        }
    }
}