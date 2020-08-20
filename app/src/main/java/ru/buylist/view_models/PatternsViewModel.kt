package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.Pattern
import ru.buylist.data.entity.PatternWrapper
import ru.buylist.data.repositories.pattern.PatternsDataSource

class PatternsViewModel(private val repository: PatternsDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var fabIsShown = ObservableBoolean(true)
    var isEditable: Boolean = false
    var patternTitle = ObservableField("")

    var wrappedPatterns = MutableLiveData<List<PatternWrapper>>().apply { value = emptyList() }
    var patterns = mutableListOf<Pattern>()

    init {
        loadList()
    }

    fun save() {
        val newPattern = Pattern()
        val title = patternTitle.get().toString().trim()
        if (title.isNotEmpty())  {
            newPattern.title = title
        }
        repository.savePattern(newPattern)
        patterns.add(newPattern)
        updateUi()
        patternTitle.set("")
    }

    fun saveEditedData(wrapper: PatternWrapper, newTitle: String) {
        val list = extractDataFromWrappedPatterns()
        wrapper.pattern.title = newTitle
        patterns[wrapper.position].title = newTitle

        updateWrappedPatterns(list, wrapper)
        repository.updatePattern(wrapper.pattern)
        isEditable = false
    }

    fun edit(wrapper: PatternWrapper) {
        val list = extractDataFromWrappedPatterns()
        checkEditableField(list)
        updateWrappedPatterns(list, wrapper, true)
        isEditable = true
    }

    fun delete(wrapper: PatternWrapper) {
        patterns.remove(wrapper.pattern)
        repository.deletePattern(wrapper.pattern)
        updateUi()
    }

    fun showHideFab(dy: Int) {
        fabIsShown.set(dy <= 0)
    }

    private fun updateUi() {
        wrappedPatterns.value = getWrappedPatterns(patterns)
        listIsEmpty.set(patterns.isEmpty())
    }

    private fun updateWrappedPatterns(list: MutableList<PatternWrapper>, wrapper: PatternWrapper,
                                      isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = wrapper.copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(wrapper.position)
        list.add(wrapper.position, newWrapper)
        wrappedPatterns.value = list
    }

    private fun checkEditableField(list: MutableList<PatternWrapper>) {
        if (isEditable) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val wrappedPattern = iterator.next()
                if (wrappedPattern.isEditable) {
                    updateWrappedPatterns(list, wrappedPattern)
                    break
                }
            }
        }
    }

    private fun extractDataFromWrappedPatterns(): MutableList<PatternWrapper> {
        val list = mutableListOf<PatternWrapper>()
        wrappedPatterns.value?.let { list.addAll(it) }
        return list
    }

    private fun getWrappedPatterns(patterns: List<Pattern>): List<PatternWrapper> {
        val newList = mutableListOf<PatternWrapper>()
        for ((i, pattern) in patterns.withIndex()) {
            val patternWrapper = PatternWrapper(pattern.copy(), i)
            newList.add(patternWrapper)
        }
        return newList
    }

    private fun loadList() {
        repository.getPatterns(object : PatternsDataSource.LoadPatternsCallback {
            override fun onPatternsLoaded(loadedPatterns: List<Pattern>) {
                wrappedPatterns.value = getWrappedPatterns(loadedPatterns)
                patterns.clear()
                patterns.addAll(loadedPatterns)
                listIsEmpty.set(patterns.isEmpty())
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }
}