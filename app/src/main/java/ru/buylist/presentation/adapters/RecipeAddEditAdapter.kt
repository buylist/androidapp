package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.databinding.ItemBuyListDetailBinding
import ru.buylist.databinding.ItemRecipeButtonBinding
import ru.buylist.databinding.ItemRecipeCookingStepBinding
import ru.buylist.databinding.ItemRecipeHeaderBinding

class RecipeAddEditAdapter(
        private var wrappedItems: List<ItemWrapper>,
        private var wrappedCookingSteps: List<CookingStepWrapper>
) : RecyclerView.Adapter<GenericViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ViewDataBinding

        when (viewType) {
            ITEMS_HEADER -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_recipe_header,
                        parent, false)
                return ItemsHeaderHolder(binding as ItemRecipeHeaderBinding)
            }
            ITEMS -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_buy_list_detail,
                        parent, false)
                return ItemHolder(binding as ItemBuyListDetailBinding)
            }
            ITEMS_BUTTON -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_recipe_button,
                        parent, false)
                return ItemButtonHolder(binding as ItemRecipeButtonBinding)
            }
            STEPS_HEADER -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_recipe_header,
                        parent, false)
                return CookingStepHeaderHolder(binding as ItemRecipeHeaderBinding)
            }
            STEPS -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_recipe_cooking_step,
                        parent, false)
                return CookingStepHolder(binding as ItemRecipeCookingStepBinding)
            }

            // STEP_BUTTON
            else -> {
                binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_recipe_button,
                        parent, false)
                return CookingStepButtonHolder(binding as ItemRecipeButtonBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = wrappedItems.size + 2 + wrappedCookingSteps.size + 2

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return ITEMS_HEADER
        if (position > 0 && position <= wrappedItems.size) return ITEMS
        if (position == wrappedItems.size + 1) return ITEMS_BUTTON
        if (position == wrappedItems.size + 2) return STEPS_HEADER
        if (position > wrappedItems.size + 2 && position <= wrappedCookingSteps.size + 1 + wrappedItems.size + 2) return STEPS
        if (position == wrappedItems.size + 2 + wrappedCookingSteps.size + 2) return STEPS_BUTTON
        return super.getItemViewType(position)
    }

    fun setData(newItems: List<ItemWrapper>, newSteps: List<CookingStepWrapper>) {
        val itemsDiffCallback = ItemsDiffCallback(wrappedItems, newItems)
        val stepsDiffCallback = CookingStepDiffCallback(wrappedCookingSteps, newSteps)
        val itemsDiffResult = DiffUtil.calculateDiff(itemsDiffCallback)
        val stepsDiffResult = DiffUtil.calculateDiff(stepsDiffCallback)
        wrappedItems = newItems
        wrappedCookingSteps = newSteps
        itemsDiffResult.dispatchUpdatesTo(this)
        stepsDiffResult.dispatchUpdatesTo(this)
    }

    private fun setupItemListener(binding: ItemBuyListDetailBinding) {

    }

    private fun setupCookingStepListener(binding: ItemRecipeCookingStepBinding) {

    }


    private inner class ItemsHeaderHolder(binding: ItemRecipeHeaderBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    private inner class ItemHolder(binding: ItemBuyListDetailBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    private inner class ItemButtonHolder(binding: ItemRecipeButtonBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    private inner class CookingStepHeaderHolder(binding: ItemRecipeHeaderBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    private inner class CookingStepHolder(binding: ItemRecipeCookingStepBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    private inner class CookingStepButtonHolder(binding: ItemRecipeButtonBinding) :
            GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            TODO("TODO")
        }

    }

    companion object {
        const val ITEMS_HEADER = 1
        const val ITEMS = 2
        const val ITEMS_BUTTON = 3
        const val STEPS_HEADER = 4
        const val STEPS = 5
        const val STEPS_BUTTON = 6
    }


}

interface CookingStepListener {
    fun onButtonMoreClick(stepWrapper: CookingStepWrapper)

    fun onButtonSaveClick(stepWrapper: CookingStepWrapper)
}

class ItemsDiffCallback(
        private val oldList: List<ItemWrapper>,
        private val newList: List<ItemWrapper>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].item.id == newList[newItemPosition].item.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}

class CookingStepDiffCallback(
        private val oldList: List<CookingStepWrapper>,
        private val newList: List<CookingStepWrapper>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}