package ru.buylist.presentation.recipe_add_edit

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.buylist.R
import ru.buylist.data.wrappers.CookingStepWrapper
import ru.buylist.databinding.RecipeCookingStepAddEditBinding
import ru.buylist.presentation.GenericViewHolder
import ru.buylist.utils.hideKeyboard

/**
 * Adapter for the cooking step on recipe add/edit screen.
 */
class RecipeAddEditStepsAdapter(val viewModel: RecipeAddEditViewModel) :
        ListAdapter<CookingStepWrapper, GenericViewHolder>(RecipeStepsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: RecipeCookingStepAddEditBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipe_cooking_step_add_edit,
                parent, false)
        return RecipeStepsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class RecipeStepsViewHolder(val binding: RecipeCookingStepAddEditBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val wrapper = getItem(position)
            binding.tvNumberOfStep.text = itemView.context
                    .getString(R.string.number_of_step, wrapper.step.number)
            binding.item = wrapper
            binding.card.setBackgroundColor(if (wrapper.isEditable) Color.WHITE else 0)
            binding.callback = getListener(itemView.context, binding.btnMore, binding.fieldStep)
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View, field: EditText): CookingStepListener {
            return object : CookingStepListener {
                override fun onButtonMoreClick(wrapper: CookingStepWrapper) {
                    PopupMenu(context, btnMore).apply {
                        menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> {
                                    viewModel.editStep(wrapper)
                                }
                                R.id.delete -> viewModel.deleteStep(wrapper)
                            }
                            true
                        }
                        show()
                    }
                }

                override fun onButtonSaveClick(wrapper: CookingStepWrapper) {
                    viewModel.saveEditedStep(wrapper, field.text.toString())
                    field.hideKeyboard()
                }

            }
        }

    }
}


/**
 * DiffUtil
 */
class RecipeStepsDiffCallback : DiffUtil.ItemCallback<CookingStepWrapper>() {
    override fun areItemsTheSame(oldItem: CookingStepWrapper, newItem: CookingStepWrapper): Boolean {
        return oldItem.step == newItem.step
    }

    override fun areContentsTheSame(oldItem: CookingStepWrapper, newItem: CookingStepWrapper): Boolean {
        return oldItem == newItem
    }

}


/**
 * Callbacks
 */
interface CookingStepListener {
    fun onButtonMoreClick(wrapper: CookingStepWrapper)
    fun onButtonSaveClick(wrapper: CookingStepWrapper)
}