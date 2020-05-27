package ru.buylist.presentation.fragments

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_page.*
import ru.buylist.presentation.BaseFragment
import ru.buylist.R

class PatternFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_page

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_info.text = "List of patterns is empty"
    }


}