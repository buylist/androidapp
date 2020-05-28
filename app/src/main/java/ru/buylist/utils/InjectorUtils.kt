package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListDataSource
import ru.buylist.data.repositories.buyList.BuyListRepository

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    private fun getBuyListRepository(context: Context) : BuyListDataSource {
        return BuyListRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().buyListDao())
    }
}