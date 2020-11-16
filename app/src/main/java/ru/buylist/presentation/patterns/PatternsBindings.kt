package ru.buylist.presentation.patterns

import android.view.inputmethod.EditorInfo
import android.widget.EditText
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

    @BindingAdapter("app:imeActionDoneListener")
    @JvmStatic
    fun setEditorActionListener(field: EditText, viewModel: PatternsViewModel) {
        field.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.save()
                    true
                }
                else -> false
            }
        }
    }

    @BindingAdapter("app:onScrolled")
    @JvmStatic
    fun setScrollListener(recycler: RecyclerView, viewModel: PatternsViewModel) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }
}