package ru.buylist.presentation.product_dictionary

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_product_dictionary.*
import ru.buylist.R
import ru.buylist.data.wrappers.CircleWrapper
import ru.buylist.databinding.FragmentProductDictionaryBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.circle_adapter.CircleItemClickListener
import ru.buylist.presentation.circle_adapter.CirclesAdapter
import ru.buylist.utils.EventObserver
import ru.buylist.utils.getViewModelFactory
import ru.buylist.utils.hideKeyboard
import ru.buylist.utils.showKeyboard

class ProductDictionaryFragment : BaseFragment<FragmentProductDictionaryBinding>() {

    private val viewModel: ProductDictionaryViewModel by viewModels { getViewModelFactory() }

    override val layoutResId: Int = R.layout.fragment_product_dictionary


    override fun setupBindings(binding: FragmentProductDictionaryBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.start(resources.getStringArray(R.array.category_color).toList())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.addProductEvent.observe(viewLifecycleOwner, EventObserver {
            expandFab()
        })

        viewModel.productCreated.observe(viewLifecycleOwner, EventObserver {
            minimizeFab()
        })
    }

    private fun setupAdapter() {
        val productsAdapter = ProductDictionaryAdapter(viewModel)
        recycler.adapter = productsAdapter

        val circlesAdapter = CirclesAdapter(circlesCallback)
        recycler_circles.adapter = circlesAdapter

        recycler_circles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun  onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideArrows(isFirstCircleVisible(), isLastCircleVisible(circlesAdapter))
            }
        })
    }

    private fun expandFab() {
        val transition = buildContainerTransform().apply {
            startView = fab_add
            endView = layout_new_item
            addTarget(layout_new_item)
        }

        TransitionManager.beginDelayedTransition(requireActivity()
                .findViewById(android.R.id.content), transition)
        layout_new_item.visibility = View.VISIBLE
        shadow_view.visibility = View.VISIBLE
        fab_add.visibility = View.GONE
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
        field_name.hideKeyboard()
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
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

    private val circlesCallback = object : CircleItemClickListener {
        override fun onCircleClick(circleWrapper: CircleWrapper) {
            viewModel.updateCircle(circleWrapper)
        }
    }
}