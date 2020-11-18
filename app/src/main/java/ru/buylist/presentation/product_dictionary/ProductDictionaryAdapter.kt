package ru.buylist.presentation.product_dictionary

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem
import ru.buylist.databinding.ItemProductDictionaryBinding
import ru.buylist.presentation.GenericViewHolder


/**
 * Adapter for the products on product dictionary screen.
 */
class ProductDictionaryAdapter(
        private val viewModel: ProductDictionaryViewModel
) : ListAdapter<GlobalItem, GenericViewHolder>(ProductDictionaryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemProductDictionaryBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_product_dictionary,
                parent, false
        )
        return ProductDictionaryHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class ProductDictionaryHolder(private val binding: ItemProductDictionaryBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.viewModel = viewModel
            binding.imgCircle.setColorFilter(Color.parseColor(item.color))
        }

    }
}


/**
 * DiffUtil
 */
class ProductDictionaryDiffCallback : DiffUtil.ItemCallback<GlobalItem>() {
    override fun areItemsTheSame(oldItem: GlobalItem, newItem: GlobalItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GlobalItem, newItem: GlobalItem): Boolean {
        return oldItem == newItem
    }
}