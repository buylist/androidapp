package ru.buylist.presentation.recipe_add_edit

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_recipe_add_edit.*
import kotlinx.android.synthetic.main.fragment_recipe_add_edit.layout_new_item
import kotlinx.android.synthetic.main.fragment_recipe_add_edit.layout_new_step
import kotlinx.android.synthetic.main.layout_new_cooking_step.*
import kotlinx.android.synthetic.main.layout_new_ingredient.*
import ru.buylist.R
import ru.buylist.data.entity.GlobalItem
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.databinding.FragmentRecipeAddEditBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.circle_adapter.CircleItemClickListener
import ru.buylist.presentation.circle_adapter.CirclesAdapter
import ru.buylist.presentation.tag_adapter.WordTipsAdapter
import ru.buylist.presentation.tag_adapter.WordTipsListener
import ru.buylist.utils.*

/**
 * Recipe add/edit screen.
 */

class RecipeAddEditFragment : BaseFragment<FragmentRecipeAddEditBinding>() {

    private val args: RecipeAddEditFragmentArgs by navArgs()

    private val viewModel: RecipeAddEditViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_recipe_add_edit

    private lateinit var circlesAdapter: CirclesAdapter

    override fun setupBindings(binding: FragmentRecipeAddEditBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        viewModel.start(args.recipeId, resources.getStringArray(R.array.category_color).toList())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSnackbar()

        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { recipe ->
            val action = RecipeAddEditFragmentDirections.actionRecipeAddEditFragmentToRecipeDetailFragment(recipe.id, recipe.title)
            findNavController().navigate(action)
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText)
    }

    private fun setupAdapter() {
        val generalHeaderAdapter = RecipeHeaderAdapter(getString(R.string.tv_general_header))
        val generalInfoAdapter = RecipeAddEditGeneralInfoAdapter(viewModel)
        val itemsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.ingredient_text))
        val itemsAdapter = RecipeAddEditItemsAdapter(viewModel)
        val itemsButtonAdapter = RecipeButtonAdapter(getString(R.string.btn_new_ingredient_description), newItemButtonCallback)
        val stepsHeaderAdapter = RecipeHeaderAdapter(getString(R.string.cooking_steps_text))
        val stepsAdapter = RecipeAddEditStepsAdapter(viewModel)
        val stepsButtonAdapter = RecipeButtonAdapter(getString(R.string.add_new_step), newStepButtonCallback)
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

        circlesAdapter = CirclesAdapter(circleCallback)
        recycler_circles.apply { adapter = circlesAdapter }

        recycler_circles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideArrows(isFirstCircleVisible(), isLastCircleVisible(circlesAdapter))
            }
        })

        val wordTipsAdapter = WordTipsAdapter(wordTipCallback)
        val flexManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            alignItems = AlignItems.CENTER
        }

        recycler_word_tips.apply {
            layoutManager = flexManager
            adapter = wordTipsAdapter
        }
    }

    private fun isLastCircleVisible(circlesAdapter: CirclesAdapter): Boolean {
        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
        val position = layoutManager.findLastVisibleItemPosition()
        return (position >= circlesAdapter.itemCount - 1)
    }

    private fun isFirstCircleVisible(): Boolean {
        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        return position > 0
    }

    private fun expand(start: View, end: View, field: EditText) {
        val transition = buildContainerTransform().apply {
            startView = start
            endView = end
            addTarget(end)
        }

        TransitionManager.beginDelayedTransition(requireActivity().findViewById(android.R.id.content), transition)
        end.visibility = View.VISIBLE
        shadow_view.visibility = View.VISIBLE
        start.visibility = View.GONE
        field.showKeyboard()
    }

    private fun minimize(start: View, end: View, field: EditText) {
        val transition = buildContainerTransform().apply {
            startView = start
            endView = end
            addTarget(end)
        }
        TransitionManager.beginDelayedTransition(coordinator_layout, transition)
        start.visibility = View.GONE
        shadow_view.visibility = View.GONE
        end.visibility = View.VISIBLE
        field.hideKeyboard()
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
            }

    private val newItemButtonCallback = object : RecipeButtonListener {
        override fun onButtonClick(view: View) {
            expand(view, layout_new_item, field_name)
            shadow_view.setOnClickListener { minimize(layout_new_item, view, field_name) }
        }

    }

    private val newStepButtonCallback = object : RecipeButtonListener {
        override fun onButtonClick(view: View) {
            expand(view, layout_new_step, field_new_step)

            shadow_view.setOnClickListener { minimize(layout_new_step, view, field_new_step) }

            btn_create_step.setOnClickListener {
                minimize(layout_new_step, view, field_new_step)
                viewModel.addNewStep()
            }
        }

    }

    private val circleCallback = object : CircleItemClickListener {
        override fun onCircleClick(circleWrapper: CircleWrapper) {
            viewModel.updateCircle(circleWrapper)
        }
    }

    private val wordTipCallback = object : WordTipsListener {

        override fun onWordTipClick(wordTip: GlobalItem) {
            viewModel.setProductInfoByWordTip(wordTip)
        }
    }
}