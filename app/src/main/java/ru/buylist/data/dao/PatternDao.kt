package ru.buylist.data.dao

import androidx.room.*
import ru.buylist.data.entity.Pattern

@Dao
interface PatternDao {

    @Insert
    fun insertPattern(pattern: Pattern)

    @Update
    fun updatePattern(pattern: Pattern)

    @Delete
    fun deletePattern(pattern: Pattern)

    @Delete
    fun deleteSelectedPatterns(patterns: List<Pattern>)

    @Query("DELETE FROM patterns")
    fun deleteAllPatterns()

    @Query("SELECT * FROM patterns")
    fun getPatterns(): List<Pattern>

    @Query("SELECT * FROM patterns WHERE id = :patternId")
    fun getPattern(patternId: Long): Pattern

}