package ru.buylist.presentation.circle_adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.wrappers.CircleWrapper

object CirclesBindings {

    @BindingAdapter("app:circles")
    @JvmStatic
    fun setCircles(recycler: RecyclerView, circles: List<CircleWrapper>?) {
        circles?.let {
            (recycler.adapter as CirclesAdapter).submitList(circles)
        }
    }
}