package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.databinding.RecipeCookingStepDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder

class RecipeStepsAdapter : ListAdapter<CookingStepWrapper, GenericViewHolder>(RecipeStepsDiffCallback()) {

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



    // ViewHolder
    private inner class RecipeStepsViewHolder(val binding: RecipeCookingStepDetailBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val wrapper = getItem(position)
            binding.tvNumberOfStep.text = itemView.context
                    .getString(R.string.number_of_step, wrapper.step.number)
            binding.wrapper = wrapper
            binding.executePendingBindings()
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