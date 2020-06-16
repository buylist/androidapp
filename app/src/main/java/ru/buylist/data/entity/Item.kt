package ru.buylist.data.entity


data class Item(
        var id: Long = System.currentTimeMillis(),
        var globalItemId: Long,
        var name: String,
        var quantity: String,
        var isPurchased: Boolean = false
) {
    val isEmpty get() = name.isEmpty()
}