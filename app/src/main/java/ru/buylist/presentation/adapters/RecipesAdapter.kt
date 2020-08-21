package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.RecipeWrapper
import ru.buylist.databinding.ItemRecipeBinding
import ru.buylist.view_models.RecipeViewModel

class RecipesAdapter(
        list: List<RecipeWrapper>,
        private val viewModel: RecipeViewModel
) : ListAdapter<RecipeWrapper, GenericViewHolder>(RecipesDiffCallback()) {

    var list: List<RecipeWrapper> = list
        set(list) {
            field = list
            submitList(list)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe,
                parent, false)

        val listener = object : RecipeItemListener {
            override fun onRecipeClicked(wrapper: RecipeWrapper) {
                showDetail(wrapper.recipe, binding.root)
            }

            override fun onButtonMoreClick(wrapper: RecipeWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
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

            override fun onButtonSaveClick(wrapper: RecipeWrapper) {
                viewModel.saveEditedData(wrapper, "")
            }

        }

        binding.callback = listener
        return RecipeHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun showDetail(recipe: Recipe, view: View) {

    }


    private inner class RecipeHolder(private val binding: ItemRecipeBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.wrapper = list[position]
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

    fun onButtonSaveClick(wrapper: RecipeWrapper)
}
