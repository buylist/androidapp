package ru.buylist.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.repositories.recipe.RecipesDataSource

class RecipeDetailViewModel(
        private val repository: RecipesDataSource,
        private val recipeId: Long) : ViewModel() {

    val fabIsShown = MutableLiveData<Boolean>(true)
}