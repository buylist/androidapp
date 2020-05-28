package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lists")
data class List(
        @PrimaryKey var id: Long = System.currentTimeMillis(),
        var title: String = "",
        var items: String = ""
)