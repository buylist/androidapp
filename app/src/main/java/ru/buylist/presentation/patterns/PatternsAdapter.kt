package ru.buylist.presentation.patterns

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
import ru.buylist.data.wrappers.PatternWrapper
import ru.buylist.databinding.ItemPatternBinding
import ru.buylist.presentation.GenericViewHolder


/**
 * Adapter for the patterns on the patterns screen.
 */
class PatternsAdapter(
        private val viewModel: PatternsViewModel
) : ListAdapter<PatternWrapper, GenericViewHolder>(PatternsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemPatternBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_pattern,
                parent, false)
        return PatternHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class PatternHolder(private val binding: ItemPatternBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.wrapper = item
            binding.callback = getListener(
                    itemView.context,
                    binding.btnMore,
                    binding.fieldPatternTitle
            )
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View, field: EditText)
                : PatternItemListener {
            return object : PatternItemListener {
                override fun onPatternClicked(wrapper: PatternWrapper) {
                    viewModel.showDetail(wrapper.pattern)
                }

                override fun onButtonMoreClick(wrapper: PatternWrapper) {
                    PopupMenu(context, btnMore).apply {
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
                    viewModel.saveEditedData(wrapper, field.text.toString())
                }

            }
        }
    }
}


/**
 * DiffUtil
 */
class PatternsDiffCallback : DiffUtil.ItemCallback<PatternWrapper>() {
    override fun areItemsTheSame(oldItem: PatternWrapper, newItem: PatternWrapper): Boolean {
        return oldItem.pattern.id == newItem.pattern.id
    }

    override fun areContentsTheSame(oldItem: PatternWrapper, newItem: PatternWrapper): Boolean {
        return oldItem == newItem
    }

}


/**
 * Callbacks
 */
interface PatternItemListener {

    fun onPatternClicked(wrapper: PatternWrapper)

    fun onButtonMoreClick(wrapper: PatternWrapper)

    fun onButtonSaveClick(wrapper: PatternWrapper)
}