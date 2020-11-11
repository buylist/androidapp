package ru.buylist.view_models.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.presentation.recipe_detail.RecipeDetailViewModel

class RecipeDetailViewModelFactory(
        private val repository: RecipesDataSource) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            RecipeDetailViewModel(repository) as T
}