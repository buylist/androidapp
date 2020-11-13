package ru.buylist.presentation.circle_adapter


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.databinding.ItemCircleBinding
import ru.buylist.presentation.GenericViewHolder


/**
 * Adapter for the product color.
 */
class CirclesAdapter(
        val listener: CircleItemClickListener
) : ListAdapter<CircleWrapper, GenericViewHolder>(CirclesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemCircleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_circle,
                parent, false)

        binding.callback = listener
        return CirclesHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class CirclesHolder(private val binding: ItemCircleBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
           val item = getItem(position)
            binding.item = item
            binding.circle.setColorFilter(Color.parseColor(item.color))
        }
    }

}


/**
 * DiffUtil
 */
class CirclesDiffCallback : DiffUtil.ItemCallback<CircleWrapper>() {
    override fun areItemsTheSame(oldItem: CircleWrapper, newItem: CircleWrapper): Boolean {
        return oldItem.color == newItem.color
    }

    override fun areContentsTheSame(oldItem: CircleWrapper, newItem: CircleWrapper): Boolean {
        return oldItem.isSelected == newItem.isSelected
    }
}


/**
 * Callbacks
 */
interface CircleItemClickListener {
    fun onCircleClick(circleWrapper: CircleWrapper)
}