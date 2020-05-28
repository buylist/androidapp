package ru.buylist.data.repositories.pattern

import ru.buylist.data.entity.Pattern

interface PatternDataSource {

    interface GetPatternCallback {
        fun onPatternLoaded(pattern: Pattern)
        fun onDataNotAvailable()
    }

    interface LoadPatternsCallback {
        fun onPatternsLoaded(patterns: List<Pattern>)
        fun onDataNotAvailable()
    }

    fun savePattern(pattern: Pattern)

    fun updatePattern(pattern: Pattern)

    fun deletePattern(pattern: Pattern)

    fun deleteSelectedPattern(patterns: List<Pattern>)

    fun deleteAllPatterns()

    fun getPatterns(callback: LoadPatternsCallback)

    fun getPattern(patternId: Long, callback: GetPatternCallback)
}