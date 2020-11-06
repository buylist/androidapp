package ru.buylist.presentation.recipe_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.CookingStep
import ru.buylist.databinding.RecipeCookingStepDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder

/**
 * Adapter for the cooking step on recipe detail screen.
 */
class RecipeDetailStepsAdapter : ListAdapter<CookingStep, GenericViewHolder>(RecipeStepsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: RecipeCookingStepDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipe_cooking_step_detail,
                parent, false)
        return RecipeStepsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class RecipeStepsViewHolder(val binding: RecipeCookingStepDetailBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val step = getItem(position)
            binding.tvNumberOfStep.text = itemView.context
                    .getString(R.string.number_of_step, step.number)
            binding.item = step
            binding.executePendingBindings()
        }

    }
}


/**
 * DiffUtil
 */
class RecipeStepsDiffCallback : DiffUtil.ItemCallback<CookingStep>() {
    override fun areItemsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean {
        return oldItem == newItem
    }

}