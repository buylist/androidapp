package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipes.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipesBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.adapters.RecipesAdapter
import ru.buylist.utils.EventObserver
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.RecipeViewModel

class RecipesFragment : BaseFragment<FragmentRecipesBinding>() {

    private val viewModel: RecipeViewModel by viewModels {
        InjectorUtils.provideRecipeViewModelFactory()
    }

    override val layoutResId: Int = R.layout.fragment_recipes

    override fun setupBindings(binding: FragmentRecipesBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add.setOnClickListener { viewModel.addNewRecipe() }

        setupAdapter()
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipesFragmentDirections
                    .actionRecipesFragmentToRecipeDetailFragment(recipe.id, recipe.title)
            findNavController().navigate(action)
        })

        viewModel.newRecipeEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipesFragmentDirections.actionRecipesFragmentToRecipeAddEditFragment(
                    recipe.id, recipe.toolbarTitle)
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val recipeAdapter = RecipesAdapter(viewModel)
        recycler.adapter = recipeAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }
}