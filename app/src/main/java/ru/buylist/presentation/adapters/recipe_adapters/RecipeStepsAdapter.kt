package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeStepsAdapter(val steps: List<CookingStepWrapper>) : ListAdapter<ItemWrapper, GenericViewHolder>(RecipeStepsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_cooking_step, parent, false)
        return RecipeStepsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    // ViewHolder
    private inner class RecipeStepsViewHolder(itemView: View) : GenericViewHolder(itemView) {
        val numberOfStep: TextView = itemView.findViewById(R.id.tv_number_of_step)
        val description: TextView = itemView.findViewById(R.id.tv_step)

        override fun bind(position: Int) {
            numberOfStep.text = "${position + 1}"
            description.text = steps[position].step.description
        }

    }
}

// Listeners
interface CookingStepListener {
    fun onButtonMoreClick(wrapper: CookingStepWrapper)
    fun onButtonSaveClick(wrapper: CookingStepWrapper)
}


// DiffCallback
class RecipeStepsDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item == newItem.item
    }

}