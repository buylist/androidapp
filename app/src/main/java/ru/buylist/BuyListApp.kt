package ru.buylist

import android.app.Application
import ru.buylist.data.db.BuyListDatabase

class BuyListApp : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    fun getDatabase(): BuyListDatabase = BuyListDatabase.getInstance(INSTANCE)

    companion object {
        private lateinit var INSTANCE : BuyListApp

        @JvmStatic
        fun get() : BuyListApp = INSTANCE
    }
}