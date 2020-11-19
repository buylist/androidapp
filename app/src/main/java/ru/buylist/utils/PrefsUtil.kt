package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp

private val prefs by lazy {
    BuyListApp.get().getSharedPreferences("BuyListPrefs", Context.MODE_PRIVATE)
}

private const val FIRST_START = "first start"

fun getFirstStart(): Boolean = prefs.getBoolean(FIRST_START, true)

fun changeFirstStart(isFirst: Boolean = false) {
    prefs.edit().putBoolean(FIRST_START, isFirst).apply()
}