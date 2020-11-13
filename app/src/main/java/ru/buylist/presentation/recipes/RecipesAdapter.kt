package ru.buylist.presentation.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.wrappers.RecipeWrapper
import ru.buylist.databinding.ItemRecipeBinding
import ru.buylist.presentation.GenericViewHolder

class RecipesAdapter(
        private val viewModel: RecipesViewModel
) : ListAdapter<RecipeWrapper, GenericViewHolder>(RecipesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe,
                parent, false)

        val listener = object : RecipeItemListener {
            override fun onRecipeClicked(wrapper: RecipeWrapper) {
                viewModel.showDetail(wrapper.recipe)
            }

            override fun onButtonMoreClick(wrapper: RecipeWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.recipe_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(wrapper)
                            R.id.delete -> viewModel.delete(wrapper)
                        }
                        true
                    }
                    show()
                }
            }
        }

        binding.callback = listener
        return RecipeHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    private inner class RecipeHolder(private val binding: ItemRecipeBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.wrapper = item
            binding.executePendingBindings()
        }
    }
}


class RecipesDiffCallback : DiffUtil.ItemCallback<RecipeWrapper>() {
    override fun areItemsTheSame(oldItem: RecipeWrapper, newItem: RecipeWrapper): Boolean {
        return oldItem.recipe.id == newItem.recipe.id
    }

    override fun areContentsTheSame(oldItem: RecipeWrapper, newItem: RecipeWrapper): Boolean {
        return oldItem == newItem
    }

}

interface RecipeItemListener {

    fun onRecipeClicked(wrapper: RecipeWrapper)

    fun onButtonMoreClick(wrapper: RecipeWrapper)
}
