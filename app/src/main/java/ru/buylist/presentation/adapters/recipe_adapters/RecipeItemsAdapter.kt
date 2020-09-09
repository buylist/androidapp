package ru.buylist.presentation.adapters.recipe_adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.ItemBuyListDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeItemsAdapter : ListAdapter<ItemWrapper, GenericViewHolder>(RecipeItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemBuyListDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list_detail,
                parent, false)
        return RecipeItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    // ViewHolder
    private inner class RecipeItemsViewHolder(val binding: ItemBuyListDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val wrapper = getItem(position)
            binding.item = wrapper
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(wrapper.item.category.color))
            binding.executePendingBindings()
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