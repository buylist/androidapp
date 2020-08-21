package ru.buylist.presentation.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.databinding.ItemCircleBinding

class CirclesAdapter(
        list: List<CircleWrapper>,
        val listener: CircleItemClickListener) :
        RecyclerView.Adapter<CirclesAdapter.CirclesHolder>() {

    var list: List<CircleWrapper> = list
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CirclesHolder {
        val binding: ItemCircleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_circle,
                parent, false)

        binding.callback = listener
        return CirclesHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CirclesHolder, position: Int) {
        val circle = list.get(position)
        holder.bind(circle)
    }

    fun updateCircles(oldPosition: Int, newPosition: Int) {
        notifyItemChanged(oldPosition)
        notifyItemChanged(newPosition)
    }

    class CirclesHolder(private val binding: ItemCircleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(circle: CircleWrapper) {
            binding.setCircle(circle)
            binding.circle.setColorFilter(Color.parseColor(circle.color))
        }
    }

}