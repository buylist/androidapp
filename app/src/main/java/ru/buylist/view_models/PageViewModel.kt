package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.buylist.collection_lists.CollectionType
import ru.buylist.data.DataRepository
import ru.buylist.data.entity.Collection

class PageViewModel(private val repository: DataRepository) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    private var listType = CollectionType.BuyList
    private var list: LiveData<MutableList<Collection>>

    init {
        list = repository.getCollection(listType)
        listIsEmpty.set(list.value.isNullOrEmpty())
    }
}