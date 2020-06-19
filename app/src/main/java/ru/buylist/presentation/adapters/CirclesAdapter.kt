package ru.buylist.presentation.adapters


import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.CircleWrapper
import ru.buylist.databinding.ItemCircleBinding
import ru.buylist.view_models.BuyListDetailViewModel

class CirclesAdapter(
        list: List<CircleWrapper>,
        private val viewModel: BuyListDetailViewModel) :
        RecyclerView.Adapter<CirclesAdapter.CirclesHolder>() {

    var list: List<CircleWrapper> = list
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    private var currentSelectedPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CirclesHolder {
        val binding: ItemCircleBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_circle,
                parent, false)

        val listener = object : CircleItemClickListener {
            override fun onCircleClick(circleWrapper: CircleWrapper) {
                viewModel.updateCircle(circleWrapper)
                notifyItemChanged(circleWrapper.position)
                notifyItemChanged(currentSelectedPosition)
                currentSelectedPosition = circleWrapper.position
            }
        }

        binding.callback = listener
        return CirclesHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CirclesHolder, position: Int) {
        val circle = list.get(position)
        holder.bind(circle)
    }

    class CirclesHolder(private val binding: ItemCircleBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(circle: CircleWrapper) {
            binding.setCircle(circle)
            binding.circle.setColorFilter(Color.parseColor(circle.color))
        }
    }

}