package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var name: String = "",
        var color: String = ""
) {
    override fun toString(): String = name
}
