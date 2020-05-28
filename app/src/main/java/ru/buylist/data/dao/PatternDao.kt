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
    fun deleteSelectedPatterns(patterns: MutableList<Pattern>)

    @Query("DELETE FROM patterns")
    fun deleteAllPatterns()

    @Query("SELECT * FROM patterns")
    fun getPatterns(): MutableList<Pattern>

    @Query("SELECT * FROM patterns WHERE id = :patternId")
    fun getPattern(patternId: Long)

}