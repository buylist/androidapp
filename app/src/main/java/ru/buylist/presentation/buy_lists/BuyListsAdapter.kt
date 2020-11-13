package ru.buylist.presentation.buy_lists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.BuyList
import ru.buylist.data.wrappers.BuyListWrapper
import ru.buylist.databinding.ItemBuyListBinding
import ru.buylist.presentation.GenericViewHolder

/**
 * Adapter for the lists on buy lists screen.
 */

class BuyListsAdapter(
        private val viewModel: BuyListsViewModel
) : ListAdapter<BuyListWrapper, GenericViewHolder>(BuyListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemBuyListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list,
                parent, false)
        return BuyListHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class BuyListHolder(private val binding: ItemBuyListBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = getListener(
                    itemView.context,
                    binding.btnMore,
                    binding.fieldBuyListTitle
            )
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View, field: EditText):
                BuyListItemListener {
            return object : BuyListItemListener {
                override fun onBuyListClicked(buyList: BuyList) {
                    viewModel.showDetail(buyList)
                }

                override fun onButtonMoreClick(buyListWrapper: BuyListWrapper) {
                    PopupMenu(context, btnMore).apply {
                        menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> viewModel.edit(buyListWrapper)
                                R.id.delete -> viewModel.delete(buyListWrapper)
                            }
                            true
                        }
                        show()
                    }
                }

                override fun onButtonSaveClick(buyListWrapper: BuyListWrapper) {
                    viewModel.saveEditedData(buyListWrapper, field.text.toString())
                }

            }
        }


    }
}


/**
 * DiffUtil
 */
class BuyListDiffCallback : DiffUtil.ItemCallback<BuyListWrapper>() {
    override fun areItemsTheSame(oldItem: BuyListWrapper, newItem: BuyListWrapper): Boolean {
        return oldItem.buyList.id == newItem.buyList.id
    }

    override fun areContentsTheSame(oldItem: BuyListWrapper, newItem: BuyListWrapper): Boolean {
        return oldItem == newItem
    }
}


/**
 * Callbacks
 */
interface BuyListItemListener {

    fun onBuyListClicked(buyList: BuyList)

    fun onButtonMoreClick(buyListWrapper: BuyListWrapper)

    fun onButtonSaveClick(buyListWrapper: BuyListWrapper)
}