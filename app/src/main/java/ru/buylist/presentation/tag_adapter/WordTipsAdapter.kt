package ru.buylist.presentation.tag_adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem
import ru.buylist.databinding.ItemWordTipBinding
import ru.buylist.presentation.GenericViewHolder


/**
 * Adapter for the tags - hints for autocomplete.
 */
class WordTipsAdapter(private val listener: WordTipsListener)
    : ListAdapter<GlobalItem, GenericViewHolder>(WordTipsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemWordTipBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_word_tip,
                parent, false
        )
        return WordTipHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class WordTipHolder(private val binding: ItemWordTipBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val item = getItem(position)
            binding.item = item
            binding.callback = listener
        }
    }
}


/**
 * DiffUtil
 */
class WordTipsDiffCallback : DiffUtil.ItemCallback<GlobalItem>() {
    override fun areItemsTheSame(oldItem: GlobalItem, newItem: GlobalItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GlobalItem, newItem: GlobalItem): Boolean {
        return oldItem == newItem
    }
}


/**
 * Callbacks
 */
interface WordTipsListener {
    fun onWordTipClick(wordTip: GlobalItem)
}