package ru.buylist.data.entity


data class Item(
        var id: Long = System.currentTimeMillis(),
        var globalItemId: Long = 0,
        var name: String,
        var quantity: String = "",
        var isPurchased: Boolean = false,
        var category: Category = Category()
) {
    val isEmpty get() = name.isEmpty()
}