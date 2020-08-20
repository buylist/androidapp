package ru.buylist.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.ItemWrapper
import ru.buylist.databinding.ItemPatternDetailBinding
import ru.buylist.view_models.PatternDetailViewModel


// TODO: в будущем сделать общий адаптер для BuyListDetail и PatterDetail
class PatternDetailAdapter(
        wrappedItems: List<ItemWrapper>,
        private val viewModel: PatternDetailViewModel
) : ListAdapter<ItemWrapper, GenericViewHolder>(PatternDetailDiffCallback()) {

    var wrappedItems: List<ItemWrapper> = wrappedItems
        set(wrappedItems) {
            field = wrappedItems
            submitList(wrappedItems)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemPatternDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pattern_detail,
                parent, false)

        val listener = object : PatternDetailItemCallback {
            override fun onButtonMoreClick(itemWrapper: ItemWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                viewModel.edit(itemWrapper)
                                binding.fieldItemTitle.requestFocus()
                            }
                            R.id.delete -> viewModel.delete(itemWrapper)
                        }
                        true
                    }
                    show()
                }
            }

            override fun onButtonSaveClick(itemWrapper: ItemWrapper) {
                viewModel.saveEditedData(itemWrapper, binding.fieldItemTitle.text.toString())
            }
        }

        binding.callback = listener
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }



    // View Holder
    private inner class ItemViewHolder(private val binding: ItemPatternDetailBinding) : GenericViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.item = wrappedItems[position]
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(wrappedItems[position].item.category.color))
            binding.executePendingBindings()
        }
    }


}


// DiffUtil
class PatternDetailDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem == newItem
    }
}


// Callback
interface PatternDetailItemCallback {
    fun onButtonMoreClick(itemWrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}