package ru.buylist.presentation.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_buy_list_detail.*
import ru.buylist.R
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.databinding.FragmentBuyListDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.adapters.BuyListDetailAdapter
import ru.buylist.presentation.adapters.CircleItemClickListener
import ru.buylist.presentation.adapters.CirclesAdapter
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.BuyListDetailViewModel

class BuyListDetailFragment : BaseFragment<FragmentBuyListDetailBinding>() {

    private val args: BuyListDetailFragmentArgs by navArgs()

    private val viewModel: BuyListDetailViewModel by viewModels {
        InjectorUtils.provideBuyListDetailViewModelFactory(args.buyListId)
    }

    override val layoutResId: Int = R.layout.fragment_buy_list_detail

    private lateinit var circlesAdapter: CirclesAdapter

    override fun setupBindings(binding: FragmentBuyListDetailBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setupCircles(resources.getStringArray(R.array.category_color).toList())
        fab_add.setOnClickListener { expandFab() }
        shadow_view.setOnClickListener { minimizeFab() }
        btn_new_item.setOnClickListener { expandNewItemLayout() }
        btn_create.setOnClickListener {
            viewModel.saveNewItem()
            field_name.requestFocus()
        }
        setupAdapter()
        setupArrowListeners()
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

    private fun setupArrowListeners() {
        val layoutManager: LinearLayoutManager = recycler_circles.layoutManager as LinearLayoutManager
        btn_prev_circles.setOnClickListener {
            val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
            if (firstVisiblePosition > 0) {
                layoutManager.smoothScrollToPosition(
                        recycler_circles, null, firstVisiblePosition - 1)
            }
        }

        btn_next_circles.setOnClickListener {
            recycler_circles.adapter?.itemCount?.let {
                if (it <= 0) return@let

                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                if (lastVisiblePosition >= it) return@let

                layoutManager.smoothScrollToPosition(
                        recycler_circles, null, lastVisiblePosition + 1)
            }

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

    private fun setupAdapter() {
        val itemsAdapter = BuyListDetailAdapter(ArrayList(0), viewModel)
        recycler_items.adapter = itemsAdapter

        recycler_items.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })

        circlesAdapter = CirclesAdapter(ArrayList(0), circlesCallback)
        recycler_circles.apply { adapter = circlesAdapter }

        recycler_circles.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun  onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideArrows(isFirstCircleVisible(), isLastCircleVisible(circlesAdapter))
            }
        })
    }

    private val circlesCallback = object : CircleItemClickListener {
        override fun onCircleClick(circleWrapper: CircleWrapper) {
            viewModel.updateCircle(circleWrapper)
            circlesAdapter.updateCircles(viewModel.getCurrentColorPosition(), circleWrapper.position)
            viewModel.saveCurrentColorPosition(circleWrapper.position)
        }

    }


}