package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.view_models.BuyListViewModel
import ru.buylist.view_models.PatternViewModel

class PatternViewModelFactory(
        private val repository: PatternsDataSource) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PatternViewModel(repository) as T
}