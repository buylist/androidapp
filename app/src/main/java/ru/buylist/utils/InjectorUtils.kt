package ru.buylist.utils

import ru.buylist.BuyListApp
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.buyList.BuyListsRepository
import ru.buylist.data.repositories.items.GlobalItemsDataSource
import ru.buylist.data.repositories.items.GlobalItemsRepository
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.pattern.PatternsRepository
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesRepository
import ru.buylist.view_models.factories.*

object InjectorUtils {

    fun getExecutors(): AppExecutors = AppExecutors()

    fun provideBuyListViewModelFactory() =
            BuyListViewModelFactory(getBuyListsRepository())

    fun providePatternViewModelFactory() =
            PatternsViewModelFactory(getPatternsRepository())

    fun provideRecipeViewModelFactory() =
            RecipesViewModelFactory(getRecipesRepository())

    fun provideBuyListDetailViewModelFactory(buyListId: Long) =
            BuyListDetailViewModelFactory(getBuyListsRepository(), getGlobalItemsRepository(), buyListId)

    fun providePatternDetailViewModelFactory(patternId: Long) =
            PatternDetailViewModelFactory(getPatternsRepository(), getGlobalItemsRepository(), patternId)

    fun provideRecipeAddEditViewModelFactory() =
            RecipeAddEditViewModelFactory(getRecipesRepository())

    fun provideRecipeDetailViewModelFactory(recipeId: Long) =
            RecipeDetailViewModelFactory(getRecipesRepository(), recipeId)

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
                BuyListApp.get().getDatabase().recipeDao())
    }

    private fun getGlobalItemsRepository(): GlobalItemsDataSource {
        return GlobalItemsRepository.getInstance(
                getExecutors(),
                BuyListApp.get().getDatabase().globalItemDao()
        )
    }
}