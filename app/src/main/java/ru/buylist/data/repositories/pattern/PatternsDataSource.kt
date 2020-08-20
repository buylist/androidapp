package ru.buylist.data.repositories.pattern

import ru.buylist.data.entity.Pattern

interface PatternsDataSource {

    interface GetPatternCallback {
        fun onPatternLoaded(loadedPattern: Pattern)
        fun onDataNotAvailable()
    }

    interface LoadPatternsCallback {
        fun onPatternsLoaded(loadedPatterns: List<Pattern>)
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