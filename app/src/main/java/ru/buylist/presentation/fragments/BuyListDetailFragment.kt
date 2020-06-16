package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_buy_list_detail.*
import ru.buylist.databinding.FragmentBuyListDetailBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.R
import ru.buylist.presentation.adapters.BuyListDetailAdapter
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

        setupAdapter()
    }

    private fun setupAdapter() {
        val itemsAdapter = BuyListDetailAdapter(ArrayList(0), viewModel)
        recycler_items.apply { adapter = itemsAdapter }
    }
}