package ru.buylist.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.ItemWrapper
import ru.buylist.databinding.ItemBuyListDetailBinding
import ru.buylist.old.buy_list.CategoryInfo
import ru.buylist.view_models.BuyListDetailViewModel

class PurchasedItemsAdapter(
        list: List<ItemWrapper>,
        private val viewModel: BuyListDetailViewModel
) : ListAdapter<ItemWrapper, PurchasedItemsAdapter.PurchasedItemsHolder>(PurchasedItemsDiffCallback()) {

    var list: List<ItemWrapper> = list
        set(list) {
            field = list
            submitList(list)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchasedItemsHolder {
        val binding: ItemBuyListDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list_detail,
                parent, false)

        val listener = object : BuyListDetailItemListener {

            override fun onItemClicked(itemWrapper: ItemWrapper) {
                viewModel.onItemClick(itemWrapper)
            }

            override fun onButtonMoreClick(itemWrapper: ItemWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(itemWrapper)
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
        return PurchasedItemsHolder(binding)
    }

    override fun onBindViewHolder(holder: PurchasedItemsHolder, position: Int) {
        val buyList = getItem(position)
        holder.bind(buyList)
    }

    class PurchasedItemsHolder(private val binding: ItemBuyListDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemWrapper: ItemWrapper) {
            binding.item = itemWrapper
            binding.card.setBackgroundColor(0)
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(CategoryInfo.COLOR))
            binding.executePendingBindings()
        }
    }
}


class PurchasedItemsDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem == newItem
    }
}


