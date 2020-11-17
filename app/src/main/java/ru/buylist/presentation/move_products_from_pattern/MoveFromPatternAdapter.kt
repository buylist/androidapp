package ru.buylist.presentation.move_products_from_pattern

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.Pattern
import ru.buylist.databinding.ItemSimplePatternBinding
import ru.buylist.presentation.GenericViewHolder


/**
 * Adapter for the patterns on the move from pattern screen.
 */
class MoveFromPatternAdapter(
        private val viewModel: MoveProductsFromPatternViewModel
) : ListAdapter<Pattern, GenericViewHolder>(MoveFromPatternsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemSimplePatternBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_simple_pattern,
                parent, false
        )
        return MoveFromPatternHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    /**
     * ViewHolder
     */
    private inner class MoveFromPatternHolder(private val binding: ItemSimplePatternBinding)
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
class MoveFromPatternsDiffCallback : DiffUtil.ItemCallback<Pattern>() {
    override fun areItemsTheSame(oldItem: Pattern, newItem: Pattern): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Pattern, newItem: Pattern): Boolean {
        return oldItem == newItem
    }

}