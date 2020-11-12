package ru.buylist.presentation.buy_lists

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_buy_lists.*
import ru.buylist.R
import ru.buylist.databinding.FragmentBuyListsBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.utils.EventObserver
import ru.buylist.utils.InjectorUtils

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

        fab_add.setOnClickListener { expandFab() }
        shadow_view.setOnClickListener { minimizeFab() }

        setupListenersToButtonsCreate()
        setupAdapter()
        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        minimizeFab()
    }

    private fun setupNavigation() {
        viewModel.detailsEvent.observe(viewLifecycleOwner, EventObserver { buyList ->
            val action = BuyListsFragmentDirections
                    .actionBuyListFragmentToBuyListDetailFragment(buyList.id, buyList.title)
            findNavController().navigate(action)
        })
    }

    private fun setupAdapter() {
        val buyListAdapter = BuyListsAdapter(viewModel)
        recycler.adapter = buyListAdapter

        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.showHideFab(dy)
            }
        })
    }

    private fun setupListenersToButtonsCreate() {
        btn_create.setOnClickListener{
            viewModel.save()
            minimizeFab()
        }

        field_name.setOnEditorActionListener { _, actionId, _ ->
            when(actionId) {
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