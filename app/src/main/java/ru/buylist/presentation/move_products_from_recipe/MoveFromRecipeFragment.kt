package ru.buylist.presentation.move_products_from_recipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_move_from_recipe.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.buylist.R
import ru.buylist.databinding.FragmentMoveFromRecipeBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.setupSnackbar

class MoveFromRecipeFragment : BaseFragment<FragmentMoveFromRecipeBinding>() {

    private val args: MoveFromRecipeFragmentArgs by navArgs()

    private val viewModel: MoveFromRecipeViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_move_from_recipe

    override fun setupBindings(binding: FragmentMoveFromRecipeBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.start(args.buyListId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().toolbar.setNavigationIcon(R.drawable.ic_close)
        setupSnackbar()
        setupAdapter()
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupAdapter() {
        val recipesAdapter = MoveFromRecipeAdapter(viewModel)
        recycler_recipes.adapter = recipesAdapter
    }
}