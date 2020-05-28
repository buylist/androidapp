package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListDataSource
import ru.buylist.data.repositories.buyList.BuyListRepository
import ru.buylist.data.repositories.items.GlobalItemDataSource
import ru.buylist.data.repositories.items.GlobalItemRepository
import ru.buylist.data.repositories.pattern.PatternDataSource
import ru.buylist.data.repositories.pattern.PatternRepository

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    private fun getBuyListRepository(context: Context): BuyListDataSource {
        return BuyListRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().buyListDao())
    }

    private fun getPatternRepository(context: Context): PatternDataSource {
        return PatternRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().patternDao()
        )
    }

    private fun getGlobalItemRepository(context: Context): GlobalItemDataSource {
        return GlobalItemRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().globalItemDao()
        )
    }
}