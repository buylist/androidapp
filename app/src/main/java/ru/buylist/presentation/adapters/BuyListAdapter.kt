package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.BuyListWrapper
import ru.buylist.databinding.ItemBuyListBinding
import ru.buylist.presentation.fragments.BuyListsFragmentDirections
import ru.buylist.view_models.BuyListViewModel

class BuyListAdapter(
        list: List<BuyListWrapper>,
        private val viewModel: BuyListViewModel
) : ListAdapter<BuyListWrapper, BuyListAdapter.BuyListHolder>(BuyListDiffCallback()) {

    var list: List<BuyListWrapper> = list
        set(list) {
            field = list
            submitList(list)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyListHolder {
        val binding: ItemBuyListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list,
                parent, false)

        val listener = object : BuyListItemListener {
            override fun onBuyListClicked(buyListWrapper: BuyListWrapper) {
                navigateToBuyList(buyListWrapper.buyList.id, binding.root)
            }

            override fun onButtonMoreClick(buyListWrapper: BuyListWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
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
                viewModel.saveEditedData(buyListWrapper, binding.fieldBuyListTitle.text.toString())
            }

        }

        binding.callback = listener
        return BuyListHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyListHolder, position: Int) {
        val buyList = getItem(position)
        holder.bind(buyList)
    }

    private fun navigateToBuyList(buyListId: Long, view: View) {
        val direction = BuyListsFragmentDirections.actionBuyListFragmentToBuyListDetailFragment(buyListId)
        view.findNavController().navigate(direction)
    }


    class BuyListHolder(private val binding: ItemBuyListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buyList: BuyListWrapper) {
            binding.item = buyList
            binding.executePendingBindings()
        }
    }
}


