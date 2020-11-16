package ru.buylist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ru.buylist.data.entity.Pattern

@Dao
interface PatternDao {

    @Insert
    suspend fun insertPattern(pattern: Pattern)

    @Update
    suspend fun updatePattern(pattern: Pattern)

    @Delete
    suspend fun deletePattern(pattern: Pattern)

    @Delete
    suspend fun deleteSelectedPatterns(patterns: List<Pattern>)

    @Query("DELETE FROM patterns")
    suspend fun deleteAllPatterns()

    @Query("SELECT * FROM patterns")
    suspend fun getPatterns(): List<Pattern>

    @Query("SELECT * FROM patterns WHERE id = :patternId")
    suspend fun getPattern(patternId: Long): Pattern

    @Query("SELECT * FROM patterns")
    fun observePatterns(): LiveData<List<Pattern>>

    @Query("SELECT items FROM patterns WHERE id =:patternId")
    fun observePatternById(patternId: Long): LiveData<String>

    @Query("UPDATE patterns SET items =:products WHERE id =:patternId")
    suspend fun updateProducts(patternId: Long, products: String)

}