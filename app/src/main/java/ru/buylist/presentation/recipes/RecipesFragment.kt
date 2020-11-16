package ru.buylist.presentation.recipes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipes.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipesBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.EventObserver
import ru.buylist.utils.InjectorUtils
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar

class RecipesFragment : BaseFragment<FragmentRecipesBinding>() {

    private val viewModel: RecipesViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_recipes

    override fun setupBindings(binding: FragmentRecipesBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupNavigation()
        setupSnackbar()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupNavigation() {
        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailFragment(recipe.id, recipe.title)
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