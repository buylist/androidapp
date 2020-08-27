package ru.buylist.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_recipe_detail.*
import ru.buylist.R
import ru.buylist.databinding.FragmentRecipeDetailBinding
import ru.buylist.presentation.BaseFragment
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

        fab_edit.setOnClickListener { expandFab() }
        setupAdapter()
    }

    private fun expandFab() {
//        val transition = buildContainerTransform().apply {
//            startView = fab_add
//            endView = layout_new_item
//            addTarget(layout_new_item)
//        }
//
//        TransitionManager.beginDelayedTransition(requireActivity().findViewById(android.R.id.content), transition)
//        layout_new_item.visibility = View.VISIBLE
//        shadow_view.visibility = View.VISIBLE
//        fab_add.visibility = View.GONE
//        requireActivity().nav_bottom.visibility = View.GONE
//        field_name.requestFocus()
    }

    private fun minimizeFab() {
//        val transition = buildContainerTransform().apply {
//            startView = layout_new_item
//            endView = fab_add
//            addTarget(fab_add)
//        }
//        TransitionManager.beginDelayedTransition(coordinator_layout, transition)
//        layout_new_item.visibility = View.GONE
//        shadow_view.visibility = View.GONE
//        layout_new_item.visibility = View.GONE
//        fab_add.visibility = View.VISIBLE
//        requireActivity().nav_bottom.visibility = View.VISIBLE
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
            }



    private fun setupAdapter() {
//        val itemsAdapter = PatternDetailAdapter(emptyList(), viewModel)
//        recycler_items.adapter = itemsAdapter
//
//        recycler_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                viewModel.showHideFab(dy)
//            }
//        })
//
//        circlesAdapter = CirclesAdapter(emptyList(), callback)
//        recycler_circles.apply { adapter = circlesAdapter }
//
//        recycler_circles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun  onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                viewModel.showHideArrows(isFirstCircleVisible(), isLastCircleVisible(circlesAdapter))
//            }
//        })
    }


}