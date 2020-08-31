package ru.buylist.presentation.adapters.recipe_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.databinding.ItemRecipeGeneralInfoDetailBinding
import ru.buylist.presentation.adapters.GenericViewHolder
import ru.buylist.view_models.RecipeDetailViewModel

class RecipeDetailGeneralInfoAdapter(val viewModel: RecipeDetailViewModel) : RecyclerView.Adapter<GenericViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeGeneralInfoDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe_general_info_detail,
                parent, false)
        return GeneralInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    private inner class GeneralInfoViewHolder(val binding: ItemRecipeGeneralInfoDetailBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.viewModel = viewModel
        }

    }
}