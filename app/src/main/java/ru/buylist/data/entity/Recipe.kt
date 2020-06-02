package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var title: String = "",
        var items: String = "",
        var description: String = ""
)