package ru.buylist.data.repositories.pattern

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.buylist.data.Result
import ru.buylist.data.Result.Error
import ru.buylist.data.Result.Success
import ru.buylist.data.dao.PatternDao
import ru.buylist.data.entity.Pattern
import ru.buylist.data.wrappers.ItemWrapper

class PatternsRepository private constructor(
        private val patternDao: PatternDao,
        private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PatternsDataSource {

    override suspend fun savePattern(pattern: Pattern) = withContext(ioDispatcher) {
        patternDao.insertPattern(pattern)
    }

    override suspend fun updatePattern(pattern: Pattern) = withContext(ioDispatcher) {
        patternDao.updatePattern(pattern)
    }

    override suspend fun deletePattern(pattern: Pattern) = withContext(ioDispatcher) {
        patternDao.deletePattern(pattern)
    }

    override suspend fun deleteSelectedPattern(patterns: List<Pattern>) = withContext(ioDispatcher) {
        patternDao.deleteSelectedPatterns(patterns)
    }

    override suspend fun deleteAllPatterns() = withContext(ioDispatcher) {
        patternDao.deleteAllPatterns()
    }

    override suspend fun getPatterns(): Result<List<Pattern>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(patternDao.getPatterns())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getPattern(patternId: Long): Result<Pattern> = withContext(ioDispatcher) {
        try {
            val pattern = patternDao.getPattern(patternId)
            if (pattern == null) {
                return@withContext Error(Exception("Шаблон не найден"))
            } else {
                return@withContext Success(pattern)
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override fun observePatterns(): LiveData<Result<List<Pattern>>> {
        return patternDao.observePatterns().map { Success(it) }
    }

    override fun observePattern(patternId: Long?): LiveData<Result<String>> {
        return if (patternId == null) {
            MutableLiveData<ItemWrapper>().map { Error(Exception("Pattern id is null")) }
        } else {
            patternDao.observePatternById(patternId).map { Success(it) }
        }
    }

    override suspend fun updateProducts(patternId: Long?, products: String) =
            withContext(ioDispatcher) {
                if (patternId == null) return@withContext
                patternDao.updateProducts(patternId, products)
            }

    companion object {
        @Volatile
        private var instance: PatternsRepository? = null

        fun getInstance(patternDao: PatternDao) =
                instance ?: synchronized(this) {
                    instance ?: PatternsRepository(patternDao).also { instance = it }
                }
    }

}