package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var title: String = "(Без названия...)",
        var items: String = "",
        var cookingSteps: String = "",
        var image: String = "",
        var category: String = "Неизвестно",
        var cookingTime: String = "Неизвестно",
        var portion: String = ""
) {
    val isEmpty
        get() = (title == "(Без названия...)" || title.isEmpty()) && items.isEmpty()

    val toolbarTitle
        get() = if (title == "(Без названия...)" || title.isEmpty()) {
            "Новый рецепт"
        } else {
            title
        }
}