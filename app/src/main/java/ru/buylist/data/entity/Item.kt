package ru.buylist.data.entity


data class Item(
        var id: Long = System.currentTimeMillis(),
        var globalItemId: Long = 0,
        var name: String,
        var quantity: String = "",
        var unit: String = "",
        var isPurchased: Boolean = false,
        var color: String = "#8A000000"
) {
    val isEmpty get() = name.isEmpty()
}