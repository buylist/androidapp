package ru.buylist.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.ItemBuyListDetailBinding
import ru.buylist.presentation.GenericViewHolder
import ru.buylist.utils.CategoryInfo
import ru.buylist.view_models.BuyListDetailViewModel

class BuyListDetailAdapter(
        wrappedItems: List<ItemWrapper>,
        private val viewModel: BuyListDetailViewModel
) : ListAdapter<ItemWrapper, GenericViewHolder>(BuyListDetailDiffCallback()) {

    var wrappedItems: List<ItemWrapper> = wrappedItems
        set(wrappedItems) {
            field = wrappedItems
            submitList(wrappedItems)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemBuyListDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list_detail,
                parent, false)

        val listener = object : BuyListDetailItemListener {

            override fun onItemClicked(itemWrapper: ItemWrapper) {
                viewModel.changePurchaseStatus(itemWrapper)
            }

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

        return when(viewType) {
            ITEMS -> ItemsHolder(binding)

            // PURCHASED_ITEMS
            else -> PurchasedItemsHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (wrappedItems[position].item.isPurchased) PURCHASED_ITEMS
        else ITEMS
    }


    private inner class ItemsHolder(private val binding: ItemBuyListDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.item = wrappedItems[position]
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(wrappedItems[position].item.category.color))
            binding.executePendingBindings()
        }

    }

    private inner class PurchasedItemsHolder(private val binding: ItemBuyListDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.item = wrappedItems[position]
            binding.card.setBackgroundColor(0)
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(CategoryInfo.COLOR))
            binding.executePendingBindings()
        }

    }


    companion object {
        const val ITEMS = 1
        const val PURCHASED_ITEMS = 2
    }
}


class BuyListDetailDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem == newItem
    }
}


