package ru.buylist.utils

import androidx.savedstate.SavedStateRegistryOwner
import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.buyList.BuyListsRepository
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.items.GlobalItemsRepository
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsRepository
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesRepository
import ru.buylist.presentation.ViewModelFactory

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    fun provideViewModel(owner: SavedStateRegistryOwner) = ViewModelFactory(
            getBuyListsRepository(),
            getPatternsRepository(),
            getRecipesRepository(),
            owner
    )

    private fun getBuyListsRepository(): BuyListsDataSource {
        return BuyListsRepository.getInstance(BuyListApp.get().getDatabase().buyListDao())
    }

    private fun getPatternsRepository(): PatternsDataSource {
        return PatternsRepository.getInstance(
                BuyListApp.get().getDatabase().patternDao())
    }

    private fun getRecipesRepository(): RecipesDataSource {
        return RecipesRepository.getInstance(
                BuyListApp.get().getDatabase().recipeDao())
    }

    private fun getGlobalItemsRepository(): GlobalItemsDataSource {
        return GlobalItemsRepository.getInstance(
                getExecutors(),
                BuyListApp.get().getDatabase().globalItemDao()
        )
    }
}