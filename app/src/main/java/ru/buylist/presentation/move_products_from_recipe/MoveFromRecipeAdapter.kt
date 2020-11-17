package ru.buylist.presentation.move_products_from_recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.Recipe
import ru.buylist.databinding.ItemSimpleRecipeBinding
import ru.buylist.presentation.GenericViewHolder

/**
 * Adapter for the recipes on the move from recipe screen.
 */
class MoveFromRecipeAdapter(
        private val viewModel: MoveFromRecipeViewModel
) : ListAdapter<Recipe, GenericViewHolder>(MoveFromRecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemSimpleRecipeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_simple_recipe,
                parent, false
        )
        return MoveFromRecipeHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class MoveFromRecipeHolder(private val binding: ItemSimpleRecipeBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

    }


}


/**
 * DiffUtil
 */
class MoveFromRecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

}