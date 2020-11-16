package ru.buylist.presentation.patterns

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Pattern
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.wrappers.PatternWrapper
import ru.buylist.utils.Event

class PatternsViewModel(private val repository: PatternsDataSource) : ViewModel() {

    private val _forceUpdate = MutableLiveData<Boolean>(false)
    private val _patternToEdit = MutableLiveData<Int>()

    private val _triggers =
            MediatorLiveData<Pair<Boolean?, Int?>>().apply {
                addSource(_forceUpdate) { value = Pair(it, _patternToEdit.value) }
                addSource(_patternToEdit) { value = Pair(_forceUpdate.value, it) }
            }

    private val _patterns: LiveData<List<PatternWrapper>> = _triggers.switchMap { pair ->
        if (pair.first == true) {
            TODO("Load patterns from remote data source")
        }
        repository.observePatterns().distinctUntilChanged().switchMap {
            loadPatterns(it, pair.second)
        }
    }

    val patterns: LiveData<List<PatternWrapper>> = _patterns

    var listIsEmpty: LiveData<Boolean> = Transformations.map(_patterns) { it.isEmpty() }

    var fabIsShown = MutableLiveData<Boolean>(true)

    var patternTitle = MutableLiveData<String>()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private val _addPatternEvent = MutableLiveData<Event<Unit>>()
    val addPatternEvent: LiveData<Event<Unit>> = _addPatternEvent

    private val _patternCreatedEvent = MutableLiveData<Event<Unit>>()
    val patternCreatedEvent: LiveData<Event<Unit>> = _patternCreatedEvent

    private val _detailsEvent = MutableLiveData<Event<Pattern>>()
    val detailsEvent: LiveData<Event<Pattern>> = _detailsEvent


    fun addNewPattern() {
        _addPatternEvent.value = Event(Unit)
    }

    fun hideNewPatternLayout() {
        _patternCreatedEvent.value = Event(Unit)
    }

    fun showDetail(pattern: Pattern) {
        _detailsEvent.value = Event(pattern)
    }

    fun save() {
        val title = patternTitle.value
        if (title == null) {
            showSnackbarMessage(R.string.snackbar_empty_pattern_title)
            return
        }

        createPattern(Pattern(title = title))
        patternTitle.value = null
        hideNewPatternLayout()
    }

    fun edit(wrapper: PatternWrapper) {
        _patternToEdit.value = wrapper.position
    }

    fun saveEditedData(wrapper: PatternWrapper, newTitle: String) {
        wrapper.pattern.title = newTitle
        viewModelScope.launch {
            repository.updatePattern(wrapper.pattern)
        }
        _patternToEdit.value = null
        fabIsShown.value = true
    }

    fun delete(wrapper: PatternWrapper) = viewModelScope.launch {
        repository.deletePattern(wrapper.pattern)
    }

    fun showHideFab(dy: Int) {
        if (_patternToEdit.value != null) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = (dy <= 0)
    }

    private fun createPattern(pattern: Pattern) = viewModelScope.launch {
        repository.savePattern(pattern)
    }

    private fun getWrappedPatterns(patterns: List<Pattern>, position: Int?): List<PatternWrapper> {
        val newList = mutableListOf<PatternWrapper>()
        for ((i, pattern) in patterns.withIndex()) {
            val patternWrapper = PatternWrapper(pattern.copy(), i)
            if (position != null && position == i) {
                patternWrapper.isEditable = true
            }
            newList.add(patternWrapper)
        }
        return newList
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun loadPatterns(patternsResult: Result<List<Pattern>>, position: Int?)
            : LiveData<List<PatternWrapper>> {
        val result = MutableLiveData<List<PatternWrapper>>()

        if (patternsResult is Success) {
            viewModelScope.launch {
                result.value = getWrappedPatterns(patternsResult.data, position)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.snackbar_patterns_loading_error)
        }
        return result
    }
}