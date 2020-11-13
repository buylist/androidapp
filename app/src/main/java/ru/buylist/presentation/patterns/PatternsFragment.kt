package ru.buylist.presentation.patterns

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_patterns.*
import ru.buylist.R
import ru.buylist.databinding.FragmentPatternsBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.InjectorUtils

class PatternsFragment : BaseFragment<FragmentPatternsBinding>() {

    private val viewModel: PatternsViewModel by viewModels {
        InjectorUtils.providePatternViewModelFactory()
    }

    override val layoutResId: Int = R.layout.fragment_patterns

    override fun setupBindings(binding: FragmentPatternsBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab_add.setOnClickListener { expandFab() }
        shadow_view.setOnClickListener { minimizeFab() }

        setupListenersToButtonsCreate()
        setupAdapter()
    }

    override fun onResume() {
        super.onResume()
        minimizeFab()
    }

    private fun setupAdapter() {
        val patternAdapter = PatternsAdapter(ArrayList(0), viewModel)
        recycler.apply { adapter = patternAdapter }

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }

    private fun setupListenersToButtonsCreate() {
        btn_create.setOnClickListener {
            viewModel.save()
            minimizeFab()
        }

        field_name.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.save()
                    minimizeFab()
                    true
                }
                else -> false
            }
        }
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
        field_name.requestFocus()
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
    }

    private fun buildContainerTransform() =
            MaterialContainerTransform().apply {
                scrimColor = Color.TRANSPARENT
                duration = 500
                fadeMode = MaterialContainerTransform.FADE_MODE_IN
                interpolator = FastOutSlowInInterpolator()
            }

}