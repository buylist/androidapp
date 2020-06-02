package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_buy_list.view.*
import ru.buylist.R
import ru.buylist.data.entity.BuyList
import ru.buylist.databinding.ItemBuyListBinding
import ru.buylist.view_models.BuyListViewModel

class BuyListAdapter(
        list: List<BuyList>,
        private val viewModel: BuyListViewModel?
) : RecyclerView.Adapter<BuyListAdapter.BuyListHolder>() {

    var list: List<BuyList> = list
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyListHolder {
        val binding: ItemBuyListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_buy_list,
                parent, false)

        val listener = object : BuyListItemListener {
            override fun onBuyListClicked(buyList: BuyList) {
                Toast.makeText(parent.context, buyList.title, Toast.LENGTH_SHORT).show()
            }

            override fun onButtonMoreClick(buyList: BuyList) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel?.edit(buyList)
                            R.id.delete -> viewModel?.delete(buyList)
                        }
                        true
                    }
                    show()
                }
            }

        }

        binding.callback = listener
        return BuyListHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: BuyListHolder, position: Int) {
        val buyList = list[position]
        holder.bind(buyList)
    }


    class BuyListHolder(private val binding: ItemBuyListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(buyList: BuyList) {
            binding.item = buyList
            itemView.tv_buy_list_title.text = buyList.title
        }
    }
}
