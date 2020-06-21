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
import ru.buylist.view_models.BuyListDetailViewModel

class BuyListDetailAdapter(
        list: List<ItemWrapper>,
        private val viewModel: BuyListDetailViewModel
) : ListAdapter<ItemWrapper, BuyListDetailAdapter.BuyListDetailHolder>(BuyListDetailDiffCallback()) {

    var list: List<ItemWrapper> = list
        set(list) {
            field = list
            submitList(list)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyListDetailHolder {
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
        return BuyListDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyListDetailHolder, position: Int) {
        val buyList = getItem(position)
        holder.bind(buyList)
    }

    class BuyListDetailHolder(private val binding: ItemBuyListDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(itemWrapper: ItemWrapper) {
            binding.item = itemWrapper
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(itemWrapper.item.category.color))
            binding.executePendingBindings()
        }
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


