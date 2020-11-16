package ru.buylist.utils

import androidx.fragment.app.Fragment
import ru.buylist.presentation.ViewModelFactory

fun Fragment.getViewModelFactory(): ViewModelFactory = InjectorUtils.provideViewModel(this)
