package ru.buylist.presentation

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import ru.buylist.data.repositories.buyList.BuyListsDataSource
import ru.buylist.data.repositories.pattern.PatternsDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.buy_list_detail.BuyListDetailViewModel
import ru.buylist.presentation.buy_lists.BuyListsViewModel
import ru.buylist.presentation.pattern_detail.PatternDetailViewModel
import ru.buylist.presentation.patterns.PatternsViewModel
import ru.buylist.presentation.recipe_add_edit.RecipeAddEditViewModel
import ru.buylist.presentation.recipe_detail.RecipeDetailViewModel
import ru.buylist.presentation.recipes.RecipesViewModel


/**
 * Factory for all ViewModels.
 */
@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
        private val buyListsRepository: BuyListsDataSource,
        private val patternsRepository: PatternsDataSource,
        private val recipesRepository: RecipesDataSource,
        owner: SavedStateRegistryOwner,
        defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
    ) = with(modelClass) {
        when {
            isAssignableFrom(BuyListsViewModel::class.java) ->
                BuyListsViewModel(buyListsRepository)
            isAssignableFrom(BuyListDetailViewModel::class.java) ->
                BuyListDetailViewModel(buyListsRepository)

            isAssignableFrom(PatternsViewModel::class.java) ->
                PatternsViewModel(patternsRepository)
            isAssignableFrom(PatternDetailViewModel::class.java) ->
                PatternDetailViewModel(patternsRepository)

            isAssignableFrom(RecipesViewModel::class.java) ->
                RecipesViewModel(recipesRepository)
            isAssignableFrom(RecipeAddEditViewModel::class.java) ->
                RecipeAddEditViewModel(recipesRepository)
            isAssignableFrom(RecipeDetailViewModel::class.java) ->
                RecipeDetailViewModel(recipesRepository)
            else ->
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    } as T
}
