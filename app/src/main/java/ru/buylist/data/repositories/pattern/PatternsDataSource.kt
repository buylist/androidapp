package ru.buylist.data.repositories.pattern

import androidx.lifecycle.LiveData
import ru.buylist.data.Result
import ru.buylist.data.entity.Pattern

interface PatternsDataSource {

    suspend fun savePattern(pattern: Pattern)

    suspend fun updatePattern(pattern: Pattern)

    suspend fun deletePattern(pattern: Pattern)

    suspend fun deleteSelectedPattern(patterns: List<Pattern>)

    suspend fun deleteAllPatterns()

    suspend fun getPatterns(): Result<List<Pattern>>

    suspend fun getPattern(patternId: Long): Result<Pattern>

    fun observePatterns(): LiveData<Result<List<Pattern>>>

    fun observePattern(patternId: Long?): LiveData<Result<String>>

    suspend fun updateProducts(patternId: Long?, products: String)
}