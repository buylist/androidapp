package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.presentation.patterns.PatternsViewModel

class PatternsViewModelFactory(
        private val repository: PatternsDataSource) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PatternsViewModel(repository) as T
}