package ru.buylist.presentation.buy_list_detail

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
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.ItemBuyListDetailBinding
import ru.buylist.presentation.GenericViewHolder
import ru.buylist.utils.CategoryInfo
import ru.buylist.utils.hideKeyboard


/**
 * Adapter for the products on buy list detail screen.
 */
class BuyListDetailAdapter(
        private val viewModel: BuyListDetailViewModel
) : ListAdapter<ItemWrapper, GenericViewHolder>(BuyListDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemBuyListDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list_detail,
                parent, false)

        return when (viewType) {
            ITEMS -> ItemsHolder(binding)

            // PURCHASED_ITEMS
            else -> PurchasedItemsHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).item.isPurchased) PURCHASED_ITEMS
        else ITEMS
    }

    private fun getListener(
            context: Context, btnMore: View, fieldName: EditText, fieldQuantity: EditText,
            fieldUnit: EditText): BuyListDetailItemListener {
        return object : BuyListDetailItemListener {

            override fun onItemClicked(position: Int) {
                viewModel.changePurchaseStatus(position)
            }

            override fun onButtonMoreClick(itemWrapper: ItemWrapper) {
                PopupMenu(context, btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> {
                                viewModel.edit(itemWrapper)
                                fieldName.requestFocus()
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
                        fieldName.text.toString(),
                        fieldQuantity.text.toString(),
                        fieldUnit.text.toString()
                )
                fieldName.hideKeyboard()
            }

        }
    }


    /**
     * ViewHolder for unpurchased products
     */
    private inner class ItemsHolder(private val binding: ItemBuyListDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = getListener(
                    itemView.context,
                    binding.btnMore,
                    binding.fieldItemTitle,
                    binding.fieldQuantity,
                    binding.fieldUnit)
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(item.item.color))
            binding.executePendingBindings()
        }

    }


    /**
     * ViewHolder for purchased products
     */
    private inner class PurchasedItemsHolder(private val binding: ItemBuyListDetailBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = getListener(
                    itemView.context,
                    binding.btnMore,
                    binding.fieldItemTitle,
                    binding.fieldQuantity,
                    binding.fieldUnit)
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


/**
 * DiffUtil
 */
class BuyListDetailDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
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
interface BuyListDetailItemListener {

    fun onItemClicked(position: Int)

    fun onButtonMoreClick(itemWrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}