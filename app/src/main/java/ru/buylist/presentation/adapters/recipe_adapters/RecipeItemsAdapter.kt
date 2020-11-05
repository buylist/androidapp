package ru.buylist.presentation.adapters.recipe_adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.RecipeIngredientDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder
import ru.buylist.view_models.RecipeAddEditViewModel

class RecipeItemsAdapter(val viewModel: RecipeAddEditViewModel?) : ListAdapter<ItemWrapper, GenericViewHolder>(RecipeItemsDiffCallback()) {

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


    // ViewHolder
    private inner class RecipeItemsViewHolder(val binding: RecipeIngredientDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val wrapper = getItem(position)
            binding.item = wrapper
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(wrapper.item.category.color))
            binding.btnMore.visibility = if (viewModel == null) View.GONE else View.VISIBLE
            binding.callback = getListener(itemView.context, binding.btnMore)
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View): RecipeItemListener {
            return object : RecipeItemListener {
                override fun onButtonMoreClick(wrapper: ItemWrapper) {
                    PopupMenu(context, btnMore).apply {
                        menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> {
                                    viewModel?.editItem(wrapper)
                                }
                                R.id.delete -> viewModel?.deleteItem(wrapper)
                            }
                            true
                        }
                        show()
                    }
                }

            }
        }
    }
}


// DiffUtil
class RecipeItemsDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item == newItem.item
    }

}


interface RecipeItemListener {
    fun onButtonMoreClick(wrapper: ItemWrapper)
}