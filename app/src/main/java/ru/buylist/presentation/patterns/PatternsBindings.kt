package ru.buylist.presentation.patterns

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.data.wrappers.PatternWrapper

object PatternsBindings {

    @BindingAdapter("app:patterns")
    @JvmStatic
    fun setPatterns(recycler: RecyclerView, patterns: List<PatternWrapper>?) {
        patterns?.let {
            (recycler.adapter as PatternsAdapter).submitList(patterns)
        }
    }
}