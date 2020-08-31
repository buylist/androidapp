package ru.buylist.presentation.fragments

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
import ru.buylist.presentation.adapters.recipe_adapters.RecipeDetailGeneralInfoAdapter
import ru.buylist.presentation.adapters.recipe_adapters.RecipeHeaderAdapter
import ru.buylist.presentation.adapters.recipe_adapters.RecipeItemsAdapter
import ru.buylist.presentation.adapters.recipe_adapters.RecipeStepsAdapter
import ru.buylist.utils.EventObserver
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.RecipeDetailViewModel

class RecipeDetailFragment : BaseFragment<FragmentRecipeDetailBinding>() {

    private val args: RecipeDetailFragmentArgs by navArgs()

    private val viewModel: RecipeDetailViewModel by viewModels {
        InjectorUtils.provideRecipeDetailViewModelFactory(args.recipeId)
    }

    override val layoutResId: Int = R.layout.fragment_recipe_detail

    override fun setupBindings(binding: FragmentRecipeDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_edit.setOnClickListener { viewModel.editRecipe() }
        setupAdapter()
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.editEvent.observe(this, EventObserver { recipe ->
            val action = RecipeDetailFragmentDirections
                    .actionRecipeDetailFragmentToRecipeAddEditFragment(recipe.id, recipe.title)
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val generalInfoAdapter = RecipeDetailGeneralInfoAdapter(viewModel)
        val itemsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.ingredient_text))
        val itemsAdapter = RecipeItemsAdapter(emptyList())
        val stepsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.cooking_steps_text))
        val stepsAdapter = RecipeStepsAdapter(emptyList())
        val concatAdapter = ConcatAdapter(generalInfoAdapter, itemsHeaderAdapter, itemsAdapter,
                stepsHeaderAdapter, stepsAdapter)
        recycler.adapter = concatAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
//                viewModel.showHideFab(dy)
            }
        })
    }


}