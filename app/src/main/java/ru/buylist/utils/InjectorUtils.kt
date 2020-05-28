package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.buyList.BuyListsRepository
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.items.GlobalItemsRepository
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsRepository

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    private fun getBuyListsRepository(context: Context): BuyListsDataSource {
        return BuyListsRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().buyListDao())
    }

    private fun getPatternsRepository(context: Context): PatternsDataSource {
        return PatternsRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().patternDao()
        )
    }

    private fun getGlobalItemsRepository(context: Context): GlobalItemsDataSource {
        return GlobalItemsRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().globalItemDao()
        )
    }
}