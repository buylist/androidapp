package ru.buylist.presentation.recipe_add_edit

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.wrappers.CookingStepWrapper
import ru.buylist.data.wrappers.ItemWrapper

/**
 * Binding Adapters for the recipe add/edit screen.
 */

object RecipeAddEditBindings {

    @BindingAdapter("app:ingredients")
    @JvmStatic
    fun setIngredients(recycler: RecyclerView, items: List<ItemWrapper>?) {
        with(recycler.adapter as ConcatAdapter) {
            items?.let {
                for (adapter in adapters) {
                    if (adapter is RecipeAddEditItemsAdapter) {
                        adapter.submitList(items)
                    }
                }
            }
        }
    }

    @BindingAdapter("app:cookingStep")
    @JvmStatic
    fun setCookingStep(recycler: RecyclerView, steps: List<CookingStepWrapper>?) {
        with(recycler.adapter as ConcatAdapter) {
            steps?.let {
                for (adapter in adapters) {
                    if (adapter is RecipeAddEditStepsAdapter) {
                        adapter.submitList(steps)
                    }
                }
            }
        }
    }
}