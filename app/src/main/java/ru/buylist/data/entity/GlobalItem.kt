package ru.buylist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "global_items")
data class GlobalItem(
        @PrimaryKey var id: String = UUID.randomUUID().toString(),
        var name: String,
        var color: String
)