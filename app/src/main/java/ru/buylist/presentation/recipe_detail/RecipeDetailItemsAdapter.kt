package ru.buylist.presentation.recipe_detail

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.Item
import ru.buylist.databinding.RecipeIngredientDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder

/**
 * Adapter for the ingredients on recipe detail screen.
 */

class RecipeDetailItemsAdapter : ListAdapter<Item, GenericViewHolder>(RecipeItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: RecipeIngredientDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipe_ingredient_detail,
                parent, false)
        return RecipeItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class RecipeItemsViewHolder(val binding: RecipeIngredientDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val ingredient = getItem(position)
            binding.item = ingredient
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(ingredient.category.color))
            binding.executePendingBindings()
        }
    }
}


/**
 * DiffUtil
 */
class RecipeItemsDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

}