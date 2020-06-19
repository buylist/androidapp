package ru.buylist.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_buy_list_detail.*
import ru.buylist.databinding.FragmentBuyListDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.R
import ru.buylist.presentation.adapters.BuyListDetailAdapter
import ru.buylist.presentation.adapters.CirclesAdapter
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.BuyListDetailViewModel

class BuyListDetailFragment : BaseFragment<FragmentBuyListDetailBinding>() {

    private val args: BuyListDetailFragmentArgs by navArgs()

    private val viewModel: BuyListDetailViewModel by viewModels {
        InjectorUtils.provideBuyListDetailViewModelFactory(args.buyListId)
    }

    override val layoutResId: Int = R.layout.fragment_buy_list_detail

    override fun setupBindings(binding: FragmentBuyListDetailBinding) {
        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setupCircles(resources.getStringArray(R.array.category_color).toList())
        fab_add.setOnClickListener { expandFab() }
        shadow_view.setOnClickListener { minimizeFab() }
        btn_new_item.setOnClickListener { expandNewItemLayout() }
        setupAdapter()
    }

    private fun expandNewItemLayout() {
        val transition = buildContainerTransform().apply {
            startView = btn_new_item
            endView = layout_new_item
            addTarget(layout_new_item)
        }

        TransitionManager.beginDelayedTransition(requireActivity().findViewById(android.R.id.content), transition)
        fab_menu.visibility = View.GONE
        requireActivity().nav_bottom.visibility = View.GONE
        layout_new_item.visibility = View.VISIBLE
        field_name.requestFocus()
    }

    private fun expandFab() {
        val transition = buildContainerTransform().apply {
            startView = fab_add
            endView = fab_menu
            addTarget(fab_menu)
        }

        TransitionManager.beginDelayedTransition(requireActivity().findViewById(android.R.id.content), transition)
        fab_menu.visibility = View.VISIBLE
        shadow_view.visibility = View.VISIBLE
        fab_add.visibility = View.GONE
    }

    private fun minimizeFab() {
        val transition = buildContainerTransform().apply {
            startView = fab_menu
            endView = fab_add
            addTarget(fab_add)
        }
        TransitionManager.beginDelayedTransition(coordinator_layout, transition)
        fab_menu.visibility = View.GONE
        shadow_view.visibility = View.GONE
        layout_new_item.visibility = View.GONE
        fab_add.visibility = View.VISIBLE
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
            }

    private fun setupAdapter() {
        val itemsAdapter = BuyListDetailAdapter(ArrayList(0), viewModel)
        recycler_items.apply { adapter = itemsAdapter }

        val circlesAdapter = CirclesAdapter(ArrayList(0), viewModel)
        recycler_circles.apply { adapter = circlesAdapter }
    }
}