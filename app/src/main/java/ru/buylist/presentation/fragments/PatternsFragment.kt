package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_pattern_list.*
import ru.buylist.R
import ru.buylist.databinding.FragmentPatternListBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.presentation.adapters.PatternAdapter
import ru.buylist.utils.InjectorUtils
import ru.buylist.view_models.PatternViewModel

class PatternsFragment : BaseFragment<FragmentPatternListBinding>() {

    private val viewModel: PatternViewModel by viewModels {
        InjectorUtils.providePatternViewModelFactory()
    }

    override val layoutResId: Int = R.layout.fragment_pattern_list

    override fun setupBindings(binding: FragmentPatternListBinding) {
        binding.viewModel = viewModel
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
        val patternAdapter = PatternAdapter(ArrayList(0), viewModel)
        recycler.apply { adapter = patternAdapter }
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
        fab_add.isExpanded = true
        requireActivity().nav_bottom.visibility = View.GONE
        shadow_view.visibility = View.VISIBLE
        field_name.requestFocus()
    }

    private fun minimizeFab() {
        fab_add.isExpanded = false
        requireActivity().nav_bottom.visibility = View.VISIBLE
        shadow_view.visibility = View.GONE
    }

}