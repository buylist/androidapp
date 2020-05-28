package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "global_items")
data class GlobalItem(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var name: String = "",
        var unit: String = "",
        var category: String = "",
        var categoryColor: String = ""
)