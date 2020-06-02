package ru.buylist.view_models

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.Pattern
import ru.buylist.data.repositories.pattern.PatternsDataSource

class PatternViewModel(private val repository: PatternsDataSource) : ViewModel() {

    var listIsEmpty = ObservableBoolean(true)
    var patternTitle = ObservableField("")

    var list = ObservableArrayList<Pattern>()

    init {
        loadList()
    }

    fun save(id: Long = 0) {
        val pattern: Pattern = if (id == 0L) {
            Pattern(title = patternTitle.get().toString())
        } else {
            Pattern(id = id)
        }
        repository.savePattern(pattern)
        patternTitle.set("")
        loadList()
    }

    fun edit(pattern: Pattern) {

    }

    fun delete(pattern: Pattern) {

    }

    private fun loadList() {
        repository.getPatterns(object : PatternsDataSource.LoadPatternsCallback {
            override fun onPatternsLoaded(patterns: List<Pattern>) {
                list.clear()
                list.addAll(patterns)
                listIsEmpty.set(false)
            }

            override fun onDataNotAvailable() {
                listIsEmpty.set(true)
            }

        })
    }
}