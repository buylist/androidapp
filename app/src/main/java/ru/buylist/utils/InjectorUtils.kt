package ru.buylist.utils

import android.content.Context
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.buyList.BuyListsRepository
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.items.GlobalItemsRepository
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsRepository
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesRepository
import ru.buylist.view_models.factories.BuyListViewModelFactory
import ru.buylist.view_models.factories.PatternViewModelFactory
import ru.buylist.view_models.factories.RecipeViewModelFactory

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    fun provideBuyListViewModelFactory() =
            BuyListViewModelFactory(getBuyListsRepository())

    fun providePatternViewModelFactory() =
            PatternViewModelFactory(getPatternsRepository())

    fun provideRecipeViewModelFactory() =
            RecipeViewModelFactory(getRecipesRepository())

    private fun getBuyListsRepository(): BuyListsDataSource {
        return BuyListsRepository.getInstance(
                getExecutors(),
                BuyListApp.get().getDatabase().buyListDao())
    }

    private fun getPatternsRepository(): PatternsDataSource {
        return PatternsRepository.getInstance(
                getExecutors(),
                BuyListApp.get().getDatabase().patternDao())
    }

    private fun getRecipesRepository(): RecipesDataSource {
        return RecipesRepository.getInstance(
                getExecutors(),
                BuyListApp.get().getDatabase().recipeDao())
    }

    private fun getGlobalItemsRepository(context: Context): GlobalItemsDataSource {
        return GlobalItemsRepository.getInstance(
                getExecutors(),
                (context.applicationContext as BuyListApp).getDatabase().globalItemDao()
        )
    }
}