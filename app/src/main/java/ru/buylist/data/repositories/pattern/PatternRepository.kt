package ru.buylist.data.repositories.pattern

import ru.buylist.data.dao.PatternDao
import ru.buylist.data.entity.Pattern
import ru.buylist.utils.AppExecutors

class PatternRepository private constructor(
        private val executors: AppExecutors,
        private val patternDao: PatternDao
) : PatternDataSource {

    override fun savePattern(pattern: Pattern) {
        executors.discIO().execute { patternDao.insertPattern(pattern) }
    }

    override fun updatePattern(pattern: Pattern) {
        executors.discIO().execute { patternDao.updatePattern(pattern) }
    }

    override fun deletePattern(pattern: Pattern) {
        executors.discIO().execute { patternDao.deletePattern(pattern) }
    }

    override fun deleteSelectedPattern(patterns: List<Pattern>) {
        executors.discIO().execute { patternDao.deleteSelectedPatterns(patterns) }
    }

    override fun deleteAllPatterns() {
        executors.discIO().execute { patternDao.deleteAllPatterns() }
    }

    override fun getPatterns(callback: PatternDataSource.LoadPatternsCallback) {
        executors.discIO().execute {
            val patterns = patternDao.getPatterns()
            executors.mainThread().execute {
                if (patterns.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onPatternsLoaded(patterns)
                }
            }
        }
    }

    override fun getPattern(patternId: Long, callback: PatternDataSource.GetPatternCallback) {
        executors.discIO().execute {
            val pattern = patternDao.getPattern(patternId)
            executors.mainThread().execute {
                if (pattern == null) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onPatternLoaded(pattern)
                }
            }
        }
    }

    companion object {
        @Volatile
        private var instance: PatternRepository? = null

        fun getInstance(executors: AppExecutors, patternDao: PatternDao) =
                instance ?: synchronized(this) {
                    instance ?: PatternRepository(executors, patternDao).also { instance = it }
                }
    }

}