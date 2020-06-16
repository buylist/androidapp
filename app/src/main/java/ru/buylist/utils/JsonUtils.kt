package ru.buylist.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.buylist.data.entity.Item

object JsonUtils {

    fun convertItemsToJson(items: List<Item>) : String = Gson().toJson(items)


    fun convertItemsFromJson(items: String): List<Item> {
        if (items.isEmpty()) {
            return emptyList()
        }
        val type = object : TypeToken<List<Item>>() {}.type
        return Gson().fromJson(items, type)
    }
}