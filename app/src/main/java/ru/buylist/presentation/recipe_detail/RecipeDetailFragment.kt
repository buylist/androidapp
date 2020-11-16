package ru.buylist.presentation.recipe_detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recipe_detail.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipeDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.recipe_add_edit.RecipeHeaderAdapter
import ru.buylist.utils.EventObserver
import ru.buylist.utils.InjectorUtils
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar


/**
 * Recipe detail screen.
 */

class RecipeDetailFragment : BaseFragment<FragmentRecipeDetailBinding>() {

    private val args: RecipeDetailFragmentArgs by navArgs()

    private val viewModel: RecipeDetailViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_recipe_detail

    override fun setupBindings(binding: FragmentRecipeDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start(args.recipeId)
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
        viewModel.editEvent.observe(viewLifecycleOwner, EventObserver {
            val action = RecipeDetailFragmentDirections
                    .actionRecipeDetailFragmentToRecipeAddEditFragment(
                            args.recipeId,
                            args.recipeTitle
                    )
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val generalInfoAdapter = RecipeDetailGeneralInfoAdapter()
        val itemsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.ingredient_text))
        val itemsAdapter = RecipeDetailItemsAdapter()
        val stepsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.cooking_steps_text))
        val stepsAdapter = RecipeDetailStepsAdapter()
        val concatAdapter = ConcatAdapter(generalInfoAdapter, itemsHeaderAdapter, itemsAdapter,
                stepsHeaderAdapter, stepsAdapter)
        recycler.adapter = concatAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }


}