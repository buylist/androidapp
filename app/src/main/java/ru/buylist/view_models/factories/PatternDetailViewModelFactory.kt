package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.presentation.pattern_detail.PatternDetailViewModel

class PatternDetailViewModelFactory(
        private val patternsRepository: PatternsDataSource
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            PatternDetailViewModel(patternsRepository) as T
}