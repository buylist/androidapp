package ru.buylist.presentation.adapters.recipe_adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeItemsAdapter(var items: List<ItemWrapper>) : ListAdapter<ItemWrapper, GenericViewHolder>(RecipeItemsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_buy_list_detail, parent, false)
        return RecipeItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newItems: List<ItemWrapper>) {
        items = newItems
        submitList(items)
    }

    private inner class RecipeItemsViewHolder(itemView: View) : GenericViewHolder(itemView) {
        val circle: ImageView = itemView.findViewById(R.id.img_category_circle)
        val itemName: TextView = itemView.findViewById(R.id.tv_item_title)
        override fun bind(position: Int) {
            circle.setColorFilter(Color.parseColor(items[position].item.category.color))
            itemName.text = items[position].item.name
        }

    }
}

class RecipeItemsDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item == newItem.item
    }

}