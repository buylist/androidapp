package ru.buylist.presentation.pattern_detail

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.wrappers.ItemWrapper
import ru.buylist.databinding.ItemPatternDetailBinding
import ru.buylist.presentation.GenericViewHolder
import ru.buylist.utils.hideKeyboard


/**
 * Adapter for the products on pattern detail screen.
 */
class PatternDetailAdapter(
        private val viewModel: PatternDetailViewModel
) : ListAdapter<ItemWrapper, GenericViewHolder>(PatternDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemPatternDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pattern_detail,
                parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class ItemViewHolder(private val binding: ItemPatternDetailBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = getListener(itemView.context, binding.btnMore, binding.fieldItemTitle)
            binding.imgCategoryCircle
                    .setColorFilter(Color.parseColor(item.item.color))
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View, field: EditText)
                : PatternDetailItemCallback {
            return object : PatternDetailItemCallback {
                override fun onButtonMoreClick(itemWrapper: ItemWrapper) {
                    PopupMenu(context, btnMore).apply {
                        menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> {
                                    viewModel.edit(itemWrapper)
                                    field.requestFocus()
                                }
                                R.id.delete -> viewModel.delete(itemWrapper)
                            }
                            true
                        }
                        show()
                    }
                }

                override fun onButtonSaveClick(itemWrapper: ItemWrapper) {
                    viewModel.saveEditedData(
                            itemWrapper,
                            field.text.toString(),
                            binding.fieldQuantity.text.toString(),
                            binding.fieldUnit.text.toString()
                    )
                    field.hideKeyboard()
                }
            }
        }
    }


}


/**
 * DiffUtil
 */
class PatternDetailDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem == newItem
    }
}


/**
 * Callbacks
 */
interface PatternDetailItemCallback {
    fun onButtonMoreClick(itemWrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}