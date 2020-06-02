package ru.buylist

import android.app.Application
import ru.buylist.data.db.BuyListDatabase
import ru.buylist.utils.AppExecutors
import ru.buylist.utils.InjectorUtils

class BuyListApp : Application() {

    lateinit var executors: AppExecutors

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        executors = InjectorUtils.getExecutors()
    }

    fun getDatabase(): BuyListDatabase = BuyListDatabase.getInstance(INSTANCE, executors)

    companion object {
        private lateinit var INSTANCE : BuyListApp

        @JvmStatic
        fun get() : BuyListApp = INSTANCE
    }
}