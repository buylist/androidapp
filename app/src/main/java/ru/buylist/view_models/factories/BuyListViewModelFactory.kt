package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.presentation.buy_lists.BuyListsViewModel

class BuyListViewModelFactory(
        private val repository: BuyListsDataSource) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            BuyListsViewModel(repository) as T
}