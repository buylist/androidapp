package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var title: String = "(Без названия...)",
        var items: String = "",
        var description: String = "",
        var image: String = "",
        var category: String = "",
        var cookingTime: String = "",
        var portion: String = ""
)