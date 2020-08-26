package ru.buylist.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_recipe_add_edit.*
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.databinding.FragmentRecipeAddEditBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.adapters.*
import ru.buylist.presentation.adapters.recipe_adapters.*
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.RecipeAddEditViewModel

class RecipeAddEditFragment : BaseFragment<FragmentRecipeAddEditBinding>() {

    private val args: RecipeAddEditFragmentArgs by navArgs()

    private val viewModel: RecipeAddEditViewModel by viewModels {
        InjectorUtils.provideRecipeAddEditViewModelFactory(args.recipeId)
    }

    override val layoutResId: Int = R.layout.fragment_recipe_add_edit

    private lateinit var circlesAdapter: CirclesAdapter

    override fun setupBindings(binding: FragmentRecipeAddEditBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setupCircles(resources.getStringArray(R.array.category_color).toList())
        fab_add.setOnClickListener { expandFab() }
//        shadow_view.setOnClickListener { minimizeFab() }
//        btn_create.setOnClickListener{
//            viewModel.saveNewItem()
//            field_name.requestFocus()
//        }
        setupAdapter()
        setupArrowListeners()
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

    private fun setupArrowListeners() {
//        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
//        btn_prev_circles.setOnClickListener {
//            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
//            if (firstVisiblePosition > 0) {
//                layoutManager.smoothScrollToPosition(
//                        recycler_circles, null, firstVisiblePosition - 1)
//            }
//        }
//
//        btn_next_circles.setOnClickListener {
//            recycler_circles.adapter?.itemCount?.let {
//                if (it <= 0) return@let
//
//                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
//                if (lastVisiblePosition >= it) return@let
//
//                layoutManager.smoothScrollToPosition(
//                        recycler_circles, null, lastVisiblePosition + 1)
//            }
//
//        }
    }

    private fun setupAdapter() {
        val generalHeaderAdapter = RecipeHeaderAdapter(getString(R.string.tv_general_header))
        val generalInfoAdapter = RecipeGeneralInfoAdapter()
        val itemsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.ingredient_text))
        val itemsAdapter = RecipeItemsAdapter(emptyList())
        val itemsButtonAdapter = RecipeButtonAdapter(getString(R.string.btn_new_ingredient_description))
        val stepsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.cooking_steps_text))
        val stepsAdapter = RecipeStepsAdapter(emptyList())
        val stepsButtonAdapter = RecipeButtonAdapter(getString(R.string.add_new_step))
        val concatAdapter = ConcatAdapter(generalHeaderAdapter, generalInfoAdapter,
                itemsHeaderAdapter, itemsAdapter, itemsButtonAdapter,
                stepsHeaderAdapter, stepsAdapter, stepsButtonAdapter)
        recycler.adapter = concatAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })

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

//    private fun isLastCircleVisible(circlesAdapter: CirclesAdapter): Boolean {
//        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
//        val position = layoutManager.findLastVisibleItemPosition()
//        return (position >= circlesAdapter.itemCount - 1)
//    }

//    private fun isFirstCircleVisible(): Boolean {
//        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
//        val position = layoutManager.findFirstVisibleItemPosition()
//        return position > 0
//    }

    private val callback = object : CircleItemClickListener {
        override fun onCircleClick(circleWrapper: CircleWrapper) {
            viewModel.updateCircle(circleWrapper)
            circlesAdapter.updateCircles(viewModel.getCurrentColorPosition(), circleWrapper.position)
            viewModel.saveCurrentColorPosition(circleWrapper.position)
        }

    }
}