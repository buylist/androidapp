package ru.buylist.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_recipe.view.*
import ru.buylist.R
import ru.buylist.data.entity.Recipe
import ru.buylist.databinding.ItemRecipeBinding
import ru.buylist.view_models.RecipeViewModel

class RecipeAdapter(
        list: List<Recipe>,
        private val viewModel: RecipeViewModel
) : RecyclerView.Adapter<RecipeAdapter.RecipeHolder>() {

    var list: List<Recipe> = list
        set(list) {
            field = list
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeHolder {
        val binding: ItemRecipeBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recipe,
                parent, false)

        val listener = object : RecipeItemListener {
            override fun onRecipeClicked(recipe: Recipe) {
                Toast.makeText(parent.context, recipe.title, Toast.LENGTH_SHORT).show()
            }

            override fun onButtonMoreClick(recipe: Recipe) {
                PopupMenu(parent.context, binding.btnMore).apply {
                    menuInflater.inflate(R.menu.buy_list_item_menu, menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.edit -> viewModel.edit(recipe)
                            R.id.delete -> viewModel.delete(recipe)
                        }
                        true
                    }
                    show()
                }
            }

        }

        binding.callback = listener
        return RecipeHolder(binding)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: RecipeHolder, position: Int) {
        val recipe = list[position]
        holder.bind(recipe)
    }


    class RecipeHolder(private val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.item = recipe
            itemView.tv_recipe_title.text = recipe.title
        }
    }
}
