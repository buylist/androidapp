package ru.buylist.presentation.tag_adapter

import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem


object TagBindings {

    @BindingAdapter("app:tags")
    @JvmStatic
    fun setTags(recycler: RecyclerView, tags: List<GlobalItem>?) {
        tags?.let {
            (recycler.adapter as WordTipsAdapter).submitList(tags)
        }
    }

    @BindingAdapter("app:tagColor")
    @JvmStatic
    fun setBTagColor(view: View, color: String) {
        val wordTipBg = ContextCompat.getDrawable(view.context, R.drawable.tag_bg)
        wordTipBg?.setTint(Color.parseColor(color))
        view.background = wordTipBg
    }
}