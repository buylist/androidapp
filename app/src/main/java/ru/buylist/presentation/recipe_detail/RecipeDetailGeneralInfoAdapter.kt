package ru.buylist.presentation.recipe_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import ru.buylist.R
import ru.buylist.data.entity.Recipe
import ru.buylist.databinding.ItemRecipeGeneralInfoDetailBinding
import ru.buylist.presentation.GenericViewHolder

class RecipeDetailGeneralInfoAdapter : RecyclerView.Adapter<GenericViewHolder>() {

    private var recipe: Recipe? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder {
        val binding: ItemRecipeGeneralInfoDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe_general_info_detail,
                parent, false)
        return GeneralInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenericViewHolder, position: Int) {
        if (recipe == null) return
        holder.bind(position)
    }

    override fun getItemCount(): Int = 1

    fun setRecipe(newRecipe: Recipe) {
        recipe = newRecipe
        notifyItemChanged(0)
    }

    private inner class GeneralInfoViewHolder(val binding: ItemRecipeGeneralInfoDetailBinding)
        : GenericViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.recipe = recipe
        }

    }
}