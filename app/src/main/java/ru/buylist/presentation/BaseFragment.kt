package ru.buylist.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment <B: ViewDataBinding> : Fragment() {

    abstract val layoutResId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: B = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        setupBindings(binding)
        return binding.root
    }

    abstract fun setupBindings(binding: B)
}