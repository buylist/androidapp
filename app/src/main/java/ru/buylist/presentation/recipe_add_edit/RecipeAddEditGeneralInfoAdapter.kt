package ru.buylist.presentation.recipe_add_edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.databinding.ItemRecipeGeneralInfoInputBinding
import ru.buylist.presentation.adapters.GenericViewHolder
import ru.buylist.presentation.recipe_add_edit.RecipeAddEditViewModel

/**
 * Adapter for the general info on recipe add/edit screen.
 */

class RecipeAddEditGeneralInfoAdapter(val viewModel: RecipeAddEditViewModel)
    : RecyclerView.Adapter<GenericViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeGeneralInfoInputBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe_general_info_input,
                parent, false)
        return GeneralInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    /**
     * ViewHolder
     */
    private inner class GeneralInfoViewHolder(val binding: ItemRecipeGeneralInfoInputBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.viewModel = viewModel
        }

    }

}