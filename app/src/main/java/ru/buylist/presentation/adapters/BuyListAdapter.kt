package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.BuyListWrapper
import ru.buylist.databinding.ItemBuyListBinding
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
            override fun onBuyListClicked(buyList: BuyListWrapper) {
                Toast.makeText(parent.context, buyList.buyList.title, Toast.LENGTH_SHORT).show()
            }

            override fun onButtonMoreClick(buyList: BuyListWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(buyList)
                            R.id.delete -> viewModel.delete(buyList)
                        }
                        true
                    }
                    show()
                }
            }

            override fun onButtonSaveClick(buyList: BuyListWrapper) {
                viewModel.saveEditedData(buyList, binding.fieldBuyListTitle.text.toString())
            }

        }

        binding.callback = listener
        return BuyListHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyListHolder, position: Int) {
        val buyList = getItem(position)
        holder.bind(buyList)
    }


    class BuyListHolder(private val binding: ItemBuyListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buyList: BuyListWrapper) {
            binding.item = buyList
            binding.executePendingBindings()
        }
    }
}


