package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeButtonAdapter(val btnText: String, val listener: RecipeButtonListener) :
        RecyclerView.Adapter<GenericViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recipe_button, parent, false)
        return ButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    private inner class ButtonViewHolder(itemView: View) : GenericViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.btn_add)

        override fun bind(position: Int) {
            button.text = btnText
            button.setOnClickListener { listener.onButtonClick() }
        }

    }
}

interface RecipeButtonListener {
    fun onButtonClick()
}