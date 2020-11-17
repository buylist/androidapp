package ru.buylist.presentation.move_products_from_pattern

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.entity.Pattern

object MoveFromPatternBindings {

    @BindingAdapter("app:patterns")
    @JvmStatic
    fun setPatterns(recycler: RecyclerView, patterns: List<Pattern>?) {
        patterns?.let {
            (recycler.adapter as MoveFromPatternAdapter).submitList(patterns)
        }
    }
}