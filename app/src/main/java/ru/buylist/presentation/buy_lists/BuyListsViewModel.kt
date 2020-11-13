package ru.buylist.presentation.buy_lists

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.*
import ru.buylist.data.entity.BuyList
import ru.buylist.data.entity.wrappers.BuyListWrapper
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.utils.Event

/**
 * ViewModel for the buy lists screen.
 */
class BuyListsViewModel(private val repository: BuyListsDataSource) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)
    private val _buyListToEdit = MutableLiveData<Int>()

    private val _triggers =
            MediatorLiveData<Pair<Boolean?, Int?>>().apply {
        addSource(_forceUpdate) { value = Pair(it, _buyListToEdit.value) }
        addSource(_buyListToEdit) { value = Pair(_forceUpdate.value, it) }
    }

    private val _buyLists: LiveData<List<BuyListWrapper>> = _triggers.switchMap { triggers ->
        if (triggers.first == true) {
            TODO("Load buy lists from remote data source.")
        }
        repository.observeBuyLists().distinctUntilChanged().switchMap { loadBuyLists(it, triggers.second) }
    }

    val buyLists: LiveData<List<BuyListWrapper>> = _buyLists

    val listIsEmpty: LiveData<Boolean> = Transformations.map(_buyLists) {
        it.isEmpty()
    }

    val fabIsShown = MutableLiveData<Boolean>(true)

    val buyListTitle = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _addBuyListsEvent = MutableLiveData<Event<Unit>>()
    val addBuyList: LiveData<Event<Unit>> = _addBuyListsEvent

    private val _buyListCreated = MutableLiveData<Event<Unit>>()
    val buyListCreated: LiveData<Event<Unit>> = _buyListCreated

    private val _detailsEvent = MutableLiveData<Event<BuyList>>()
    val detailsEvent: LiveData<Event<BuyList>> = _detailsEvent

    fun addNewBuyList() {
        _addBuyListsEvent.value = Event(Unit)
    }

    fun showDetail(buyList: BuyList) {
        _detailsEvent.value = Event(buyList)
    }

    fun save() {
        val title = buyListTitle.value
        if (title == null) {
            showSnackbarMessage(R.string.snackbar_empty_buy_list_title)
            return
        }

        createBuyList(BuyList(title = title))
        buyListTitle.value = null
        hideLayoutWithFields()
    }

    fun hideLayoutWithFields() {
        _buyListCreated.value = Event((Unit))
    }

    fun edit(buyListWrapper: BuyListWrapper) {
        _buyListToEdit.value = buyListWrapper.position
    }

    fun saveEditedData(buyListWrapper: BuyListWrapper, newTitle: String) {
        buyListWrapper.buyList.title = newTitle
        viewModelScope.launch {
            repository.updateBuyList(buyListWrapper.buyList)
        }
        _buyListToEdit.value = null
        fabIsShown.value = true
    }

    fun delete(buyListWrapper: BuyListWrapper) = viewModelScope.launch {
        repository.deleteBuyList(buyListWrapper.buyList)
    }


    fun showHideFab(dy: Int) {
        if (_buyListToEdit.value != null) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = (dy <= 0)
    }

    private fun createBuyList(buyList: BuyList) = viewModelScope.launch {
        repository.saveBuyList(buyList)
    }

    private fun getWrapperList(buyLists: List<BuyList>, position: Int?)
            : List<BuyListWrapper> {
        val newList = mutableListOf<BuyListWrapper>()
        for ((i, buyList) in buyLists.withIndex()) {
            val buyListWrapper = BuyListWrapper(buyList, i)
            if (position != null && position == i) {
                buyListWrapper.isEditable = true
            }
            newList.add(buyListWrapper)
        }
        return newList
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun loadBuyLists(buyListResult: Result<List<BuyList>>, position: Int?)
            : LiveData<List<BuyListWrapper>> {
        val result = MutableLiveData<List<BuyListWrapper>>()

        if (buyListResult is Success) {
            viewModelScope.launch {
                result.value = getWrapperList(buyListResult.data, position)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.snackbar_buy_lists_loading_error)
        }
        return result
    }

}
