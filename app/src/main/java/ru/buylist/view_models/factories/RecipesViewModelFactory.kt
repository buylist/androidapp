package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.recipes.RecipesViewModel

class RecipesViewModelFactory(
        private val repository: RecipesDataSource) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            RecipesViewModel(repository) as T
}