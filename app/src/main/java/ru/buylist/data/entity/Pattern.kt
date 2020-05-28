package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class Pattern(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var title: String = "",
        var items: String = ""
)