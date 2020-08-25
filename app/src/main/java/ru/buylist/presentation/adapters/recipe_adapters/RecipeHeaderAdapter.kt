package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeHeaderAdapter(val header: String) : RecyclerView.Adapter<GenericViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_header, parent, false)
        return HeaderViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    private inner class HeaderViewHolder(itemView: View) : GenericViewHolder(itemView) {
        val tvHeader: TextView = itemView.findViewById(R.id.tv_header)
        override fun bind(position: Int) {
            tvHeader.text = header
        }

    }
}