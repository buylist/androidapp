package ru.buylist

import android.app.Application
import ru.buylist.data.db.BuyListDatabase
import ru.buylist.utils.AppExecutors
import ru.buylist.utils.InjectorUtils

class BuyListApp : Application() {

    lateinit var instance: BuyListApp
    lateinit var executors: AppExecutors

    override fun onCreate() {
        super.onCreate()
        instance = this
        executors = InjectorUtils.getExecutors()
    }

    fun getDatabase(): BuyListDatabase = BuyListDatabase.getInstance(instance, executors)
}