package ru.buylist.presentation.patterns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.Pattern
import ru.buylist.data.wrappers.PatternWrapper
import ru.buylist.databinding.ItemPatternBinding
import ru.buylist.presentation.GenericViewHolder

class PatternsAdapter(
        list: List<PatternWrapper>,
        private val viewModel: PatternsViewModel
) : ListAdapter<PatternWrapper, GenericViewHolder>(PatternsDiffCallback()) {

    var list: List<PatternWrapper> = list
        set(list) {
            field = list
            submitList(list)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemPatternBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pattern,
                parent, false)

        val listener = object : PatternItemListener {
            override fun onPatternClicked(wrapper: PatternWrapper) {
                showDetail(wrapper.pattern, binding.root)
            }

            override fun onButtonMoreClick(wrapper: PatternWrapper) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(wrapper)
                            R.id.delete -> viewModel.delete(wrapper)
                        }
                        true
                    }
                    show()
                }
            }

            override fun onButtonSaveClick(wrapper: PatternWrapper) {
                viewModel.saveEditedData(wrapper, binding.fieldPatternTitle.text.toString())
            }

        }

        binding.callback = listener
        return PatternHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    private fun showDetail(pattern: Pattern, view: View) {
        val direction = PatternsFragmentDirections.actionPatternsFragmentToPatternDetailFragment(pattern.id, pattern.title)
        view.findNavController().navigate(direction)
    }


    private inner class PatternHolder(private val binding: ItemPatternBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.wrapper = list[position]
            binding.executePendingBindings()
        }
    }
}



class PatternsDiffCallback : DiffUtil.ItemCallback<PatternWrapper>() {
    override fun areItemsTheSame(oldItem: PatternWrapper, newItem: PatternWrapper): Boolean {
        return oldItem.pattern.id == newItem.pattern.id
    }

    override fun areContentsTheSame(oldItem: PatternWrapper, newItem: PatternWrapper): Boolean {
        return oldItem == newItem
    }

}


interface PatternItemListener {

    fun onPatternClicked(wrapper: PatternWrapper)

    fun onButtonMoreClick(wrapper: PatternWrapper)

    fun onButtonSaveClick(wrapper: PatternWrapper)
}