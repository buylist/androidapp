package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pattern.view.*
import ru.buylist.R
import ru.buylist.data.entity.Pattern
import ru.buylist.databinding.ItemPatternBinding
import ru.buylist.presentation.fragments.PatternsFragmentDirections
import ru.buylist.view_models.PatternsViewModel

class PatternsAdapter(
        list: List<Pattern>,
        private val viewModel: PatternsViewModel
) : RecyclerView.Adapter<PatternsAdapter.PatternHolder>() {

    var list: List<Pattern> = list
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatternHolder {
        val binding: ItemPatternBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pattern,
                parent, false)

        val listener = object : PatternItemListener {
            override fun onPatternClicked(pattern: Pattern) {
                showDetail(pattern.id, binding.root)
            }

            override fun onButtonMoreClick(pattern: Pattern) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(pattern)
                            R.id.delete -> viewModel.delete(pattern)
                        }
                        true
                    }
                    show()
                }
            }

        }

        binding.callback = listener
        return PatternHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: PatternHolder, position: Int) {
        val pattern = list[position]
        holder.bind(pattern)
    }

    private fun showDetail(patterId: Long, view: View) {
        val direction = PatternsFragmentDirections.actionPatternsFragmentToPatternDetailFragment(patterId)
        view.findNavController().navigate(direction)
    }


    class PatternHolder(private val binding: ItemPatternBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(pattern: Pattern) {
            binding.item = pattern
            itemView.tv_pattern_title.text = pattern.title
        }
    }
}
