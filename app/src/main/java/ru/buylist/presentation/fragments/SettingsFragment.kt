package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_page.*
import ru.buylist.presentation.BaseFragment
import ru.buylist.R
import ru.buylist.databinding.FragmentPageBinding
import ru.buylist.view_models.BuyListViewModel

class SettingsFragment : BaseFragment<FragmentPageBinding>() {

    private lateinit var viewModel: BuyListViewModel

    override val layoutResId: Int = R.layout.fragment_page

    override fun setupBindings(binding: FragmentPageBinding) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_info.text = "Here will be the settings"
    }
}