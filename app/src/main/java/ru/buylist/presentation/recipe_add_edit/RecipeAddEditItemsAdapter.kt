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
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.RecipeIngredientAddEditBinding
import ru.buylist.presentation.adapters.GenericViewHolder

/**
 * Adapter for the ingredients on recipe add/edit screen.
 */

class RecipeAddEditItemsAdapter(val viewModel: RecipeAddEditViewModel) : ListAdapter<ItemWrapper, GenericViewHolder>(RecipeItemsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: RecipeIngredientAddEditBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.recipe_ingredient_add_edit,
                parent, false)
        return RecipeItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }


    /**
     * ViewHolder
     */
    private inner class RecipeItemsViewHolder(val binding: RecipeIngredientAddEditBinding) : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            val wrapper = getItem(position)
            binding.item = wrapper
            binding.card.setBackgroundColor(if (wrapper.isEditable) Color.WHITE else 0)
            binding.imgCategoryCircle.setColorFilter(Color.parseColor(wrapper.item.category.color))
            binding.callback = getListener(itemView.context, binding.btnMore, binding.fieldItemTitle)
            binding.executePendingBindings()
        }

        private fun getListener(context: Context, btnMore: View, field: EditText): RecipeItemListener {
            return object : RecipeItemListener {
                override fun onButtonMoreClick(wrapper: ItemWrapper) {
                    PopupMenu(context, btnMore).apply {
                        menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                        setOnMenuItemClickListener { item ->
                            when (item.itemId) {
                                R.id.edit -> {
                                    viewModel.editItem(wrapper)
                                    field.requestFocus()
                                    field.setSelection(field.text.length)
                                }
                                R.id.delete -> viewModel.deleteItem(wrapper)
                            }
                            true
                        }
                        show()
                    }
                }

                override fun onButtonSaveClick(itemWrapper: ItemWrapper) {
                    viewModel.saveEditedItem(itemWrapper, binding.fieldItemTitle.text.toString())
                }

            }
        }
    }
}


/**
 * DiffUtil
 */
class RecipeItemsDiffCallback : DiffUtil.ItemCallback<ItemWrapper>() {
    override fun areItemsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem.item.id == newItem.item.id
    }

    override fun areContentsTheSame(oldItem: ItemWrapper, newItem: ItemWrapper): Boolean {
        return oldItem == newItem
    }

}


/**
 * Callbacks
 */
interface RecipeItemListener {
    fun onButtonMoreClick(wrapper: ItemWrapper)

    fun onButtonSaveClick(itemWrapper: ItemWrapper)
}