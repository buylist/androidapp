package ru.buylist.presentation.buy_lists

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_buy_lists.*
import ru.buylist.R
import ru.buylist.databinding.FragmentBuyListsBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.*

/**
 * Buy lists screen.
 */
class BuyListsFragment : BaseFragment<FragmentBuyListsBinding>() {

    private val viewModel: BuyListsViewModel by viewModels {
        InjectorUtils.provideBuyListViewModelFactory()
    }

    override val layoutResId: Int = R.layout.fragment_buy_lists

    override fun setupBindings(binding: FragmentBuyListsBinding) {
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
        viewModel.addBuyList.observe(viewLifecycleOwner, EventObserver {
            expandFab()
        })

        viewModel.buyListCreated.observe(viewLifecycleOwner, EventObserver {
            minimizeFab()
        })

        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { buyList ->
            val action = BuyListsFragmentDirections
                    .actionBuyListFragmentToBuyListDetailFragment(buyList.id, buyList.title)
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val buyListAdapter = BuyListsAdapter(viewModel)
        recycler.adapter = buyListAdapter
    }

    private fun expandFab() {
        val transition = buildContainerTransform().apply {
            startView = fab_add
            endView = layout_new_item
            addTarget(layout_new_item)
        }

        TransitionManager.beginDelayedTransition(requireActivity().findViewById(android.R.id.content), transition)
        layout_new_item.visibility = View.VISIBLE
        shadow_view.visibility = View.VISIBLE
        fab_add.visibility = View.GONE
        requireActivity().nav_bottom.visibility = View.GONE
        field_name.showKeyboard()
    }

    private fun minimizeFab() {
        val transition = buildContainerTransform().apply {
            startView = layout_new_item
            endView = fab_add
            addTarget(fab_add)
        }
        TransitionManager.beginDelayedTransition(coordinator_layout, transition)
        layout_new_item.visibility = View.GONE
        shadow_view.visibility = View.GONE
        fab_add.visibility = View.VISIBLE
        requireActivity().nav_bottom.visibility = View.VISIBLE
        field_name.hideKeyboard()
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
            }
}