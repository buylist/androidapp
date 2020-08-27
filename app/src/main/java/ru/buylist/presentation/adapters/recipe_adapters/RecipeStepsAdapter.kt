package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.databinding.ItemRecipeCookingStepBinding
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeStepsAdapter(var steps: List<CookingStepWrapper>)
    : ListAdapter<CookingStepWrapper, GenericViewHolder>(RecipeStepsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeCookingStepBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe_cooking_step,
                parent, false)
        return RecipeStepsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setData(newSteps: List<CookingStepWrapper>) {
        steps = newSteps
        submitList(steps)
    }


    // ViewHolder
    private inner class RecipeStepsViewHolder(val binding: ItemRecipeCookingStepBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.tvNumberOfStep.text = itemView.context
                    .getString(R.string.number_of_step, position + 1)
            binding.wrapper = steps[position]
        }

    }
}

// Listeners
interface CookingStepListener {
    fun onButtonMoreClick(wrapper: CookingStepWrapper)
    fun onButtonSaveClick(wrapper: CookingStepWrapper)
}


// DiffCallback
class RecipeStepsDiffCallback : DiffUtil.ItemCallback<CookingStepWrapper>() {
    override fun areItemsTheSame(oldItem: CookingStepWrapper, newItem: CookingStepWrapper): Boolean {
        return oldItem.step.number == newItem.step.number
    }

    override fun areContentsTheSame(oldItem: CookingStepWrapper, newItem: CookingStepWrapper): Boolean {
        return oldItem.step == newItem.step
    }

}