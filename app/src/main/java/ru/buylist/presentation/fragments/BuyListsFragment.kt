package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.fragment_page.*
import ru.buylist.R
import ru.buylist.databinding.FragmentPageBinding
import ru.buylist.presentation.BaseFragment
import ru.buylist.view_models.PageViewModel

class BuyListsFragment : BaseFragment<FragmentPageBinding>() {

    private lateinit var viewModel: PageViewModel

    override val layoutResId: Int = R.layout.fragment_page

    override fun setupBindings(binding: FragmentPageBinding) {
//        viewModel = PageViewModel((context?.applicationContext as BuyListApp).repository)
//        binding.viewModel = viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_info.text = "List of buylists is empty"

        fab_add.show()

        fab_add.setOnClickListener {
            fab_add.isExpanded = !fab_add.isExpanded
            requireActivity().nav_bottom.visibility = View.GONE
            shadow_view.visibility = View.VISIBLE
            field_name.requestFocus()
        }

        shadow_view.setOnClickListener {
            fab_add.isExpanded = !fab_add.isExpanded
            requireActivity().nav_bottom.visibility = View.VISIBLE
            shadow_view.visibility = View.GONE
        }
    }
}