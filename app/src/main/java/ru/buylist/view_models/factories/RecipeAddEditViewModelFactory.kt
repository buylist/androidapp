package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.view_models.RecipeAddEditViewModel

class RecipeAddEditViewModelFactory(
        private val repository: RecipesDataSource,
        private val recipeId: Long) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            RecipeAddEditViewModel(repository, recipeId) as T
}