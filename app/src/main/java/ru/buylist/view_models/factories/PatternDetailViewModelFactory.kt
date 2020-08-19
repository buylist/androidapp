package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.view_models.PatternDetailViewModel

class PatternDetailViewModelFactory(
        private val patternsRepository: PatternsDataSource,
        private val globalItemsRepository: GlobalItemsDataSource,
        private val patternId: Long) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PatternDetailViewModel(patternsRepository, globalItemsRepository, patternId) as T
}