package ru.buylist.presentation.recipe_add_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.Category
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.Event
import ru.buylist.utils.JsonUtils

/**
 * ViewModel for the Add/Edit screen.
 */

class RecipeAddEditViewModel(private val repository: RecipesDataSource) : ViewModel() {

    private var _recipeId = NEW_RECIPE_ID

    private var ingredientToEdit = NO_EDIT
    private var stepToEdit = NO_EDIT
    private var colorPosition = -1

    // Fields for two-way databinding
    val recipeTitle = MutableLiveData<String>()
    val recipeCategory = MutableLiveData<String>()
    val recipeCookingTime = MutableLiveData<String>()
    val itemName = MutableLiveData<String>()
    val step = MutableLiveData<String>()

    // Flags to show and hide buttons
    val fabIsShown = MutableLiveData<Boolean>(true)
    val prevArrowIsShown = MutableLiveData<Boolean>(true)
    val nextArrowIsShown = MutableLiveData<Boolean>(true)

    // Ingredients for the recipe
    private val ingredients = mutableListOf<Item>()
    private val _wrappedIngredients = MutableLiveData<List<ItemWrapper>>()
            .apply { value = getWrappedItems(ingredients) }
    val wrappedIngredients: LiveData<List<ItemWrapper>> = _wrappedIngredients

    // Cooking steps for recipe
    private val steps = mutableListOf<CookingStep>()
    private val _wrappedSteps = MutableLiveData<List<CookingStepWrapper>>()
            .apply { value = getWrappedSteps(steps) }
    val wrappedSteps: LiveData<List<CookingStepWrapper>> = _wrappedSteps

    // Markers that show the category of an ingredient using color
    private val circles = mutableListOf<String>()
    private val _wrappedCircles = MutableLiveData<List<CircleWrapper>>()
            .apply { value = emptyList() }
    val wrappedCircles: LiveData<List<CircleWrapper>> = _wrappedCircles

    // Event that opens the recipe detail screen
    private val _detailsEvent = MutableLiveData<Event<Recipe>>()
    val detailsEvent: LiveData<Event<Recipe>> = _detailsEvent

    private lateinit var recipe: Recipe


    fun start(recipeId: Long, newCircles: List<String>) {
        _recipeId = recipeId
        setupCircles(newCircles)
        if (recipeId == NEW_RECIPE_ID) {
            return
        }

        viewModelScope.launch {
            repository.getRecipe(recipeId).let { result ->
                if (result is Success) {
                    onRecipeLoaded(result.data)
                } else {
                    onDataNotAvailable()
                }
            }
        }
    }

    fun saveRecipe() {
        val recipe = Recipe()

        val title = recipeTitle.value.toString().trim()
        if (title.isEmpty() || ingredients.isEmpty() || recipeTitle.value == null) {
            // TODO: show snackbar "Введите название рецепта и добавьте ингредиенты."
            return
        }
        recipe.title = title
        recipe.items = JsonUtils.convertItemsToJson(ingredients)

        val category = recipeCategory.value.toString().trim()
        if (category.isNotEmpty() && recipeCategory.value != null) {
            recipe.category = category
        }

        val time = recipeCookingTime.value.toString().trim()
        if (time.isNotEmpty() && recipeCookingTime.value != null) {
            recipe.cookingTime = time
        }

        if (steps.isNotEmpty()) {
            recipe.cookingSteps = JsonUtils.convertCookingStepsToJson(steps)
        }

        when (_recipeId) {
            NEW_RECIPE_ID -> createRecipe(recipe)
            else -> {
                recipe.id = _recipeId
                updateRecipe(recipe)
            }
        }
    }

    fun addNewItem() {
        val name = itemName.value.toString().trim()
        if (name.isEmpty() || itemName.value == null) return

        val item = Item(name = name, category = getCategory())

        ingredients.add(item)
        ingredients.sortWith(compareBy({ it.category.color }, { it.id }))
        updateUi()
        itemName.value = null
    }

    fun addNewStep() {
        val description = step.value.toString().trim()
        if (description.isEmpty() || step.value == null) return

        val newStep = CookingStep(steps.size + 1, description)
        steps.add(newStep)
        updateUi()
        step.value = null
    }

    fun editItem(wrapper: ItemWrapper) {
        val items = extractDataFromWrappedItems()

        if (ingredientToEdit >= 0 && ingredientToEdit < items.size) {
            updateWrappedItems(items, ingredientToEdit)
        }

        if (stepToEdit >= 0 && stepToEdit < steps.size) {
            updateWrappedSteps(extractDataFromWrappedSteps(), stepToEdit)
            stepToEdit = NO_EDIT
        }

        ingredientToEdit = wrapper.position
        updateWrappedItems(items, ingredientToEdit, isEditable = true)
    }

    fun saveEditedItem(wrapper: ItemWrapper, newName: String) {
        val list = extractDataFromWrappedItems()
        list[wrapper.position].item.name = newName
        ingredients[wrapper.position].name = newName
        updateWrappedItems(list, wrapper.position)
        ingredientToEdit = NO_EDIT
    }

    fun editStep(wrapper: CookingStepWrapper) {
        val steps = extractDataFromWrappedSteps()

        if (stepToEdit >= 0 && stepToEdit < steps.size) {
            updateWrappedSteps(steps, stepToEdit)
        }

        if (ingredientToEdit >= 0 && ingredientToEdit < ingredients.size) {
            updateWrappedItems(extractDataFromWrappedItems(), ingredientToEdit)
            ingredientToEdit = NO_EDIT
        }

        stepToEdit = wrapper.position
        updateWrappedSteps(steps, stepToEdit, isEditable = true)
    }

    fun saveEditedStep(wrapper: CookingStepWrapper, newStep: String) {
        val list = extractDataFromWrappedSteps()
        list[wrapper.position].step.description = newStep
        steps[wrapper.position].description = newStep
        updateWrappedSteps(list, wrapper.position)
        stepToEdit = NO_EDIT
    }

    fun deleteItem(wrapper: ItemWrapper) {
        val list = extractDataFromWrappedItems()
        list.removeAt(wrapper.position)
        ingredients.remove(wrapper.item)
        _wrappedIngredients.value = list
    }

    fun deleteStep(wrapper: CookingStepWrapper) {
        steps.removeAt(wrapper.position)
        _wrappedSteps.value = getWrappedSteps(steps)
    }

    fun getCurrentColorPosition() = colorPosition

    fun saveCurrentColorPosition(position: Int) {
        colorPosition = position
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        val circleList: MutableList<CircleWrapper> = _wrappedCircles.value as MutableList<CircleWrapper>
        checkSelectedCircles(circleList)
        selectedCircle.isSelected = !selectedCircle.isSelected
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.value = prev
        nextArrowIsShown.value = next
    }

    fun showHideFab(dy: Int) {
        if (ingredientToEdit > NO_EDIT || stepToEdit > NO_EDIT) {
            fabIsShown.value = false
            return
        }
        fabIsShown.value = (dy <= 0)
    }

    private fun createRecipe(newRecipe: Recipe) = viewModelScope.launch {
        repository.saveRecipe(newRecipe)
        _detailsEvent.value = Event(newRecipe)
    }

    private fun updateRecipe(updatedRecipe: Recipe) = viewModelScope.launch {
        repository.updateRecipe(updatedRecipe)
        _detailsEvent.value = Event(updatedRecipe)
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun updateUi() {
        _wrappedIngredients.value = getWrappedItems(ingredients)
        _wrappedSteps.value = getWrappedSteps(steps)
    }

    private fun updateWrappedSteps(list: MutableList<CookingStepWrapper>, position: Int,
                                   isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = list[position].copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(position)
        list.add(position, newWrapper)
        _wrappedSteps.value = list
    }

    private fun updateWrappedItems(list: MutableList<ItemWrapper>, position: Int,
                                   isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = list[position].copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(position)
        list.add(position, newWrapper)
        _wrappedIngredients.value = list
    }

    private fun checkSelectedCircles(list: MutableList<CircleWrapper>) {
        val iterator = list.iterator()
        while (iterator.hasNext()) {
            val circle = iterator.next()
            if (circle.isSelected) {
                circle.isSelected = !circle.isSelected
                break
            }
        }
    }

    private fun extractDataFromWrappedItems(): MutableList<ItemWrapper> {
        val list = mutableListOf<ItemWrapper>()
        wrappedIngredients.value?.let { list.addAll(it) }
        return list
    }

    private fun extractDataFromWrappedSteps(): MutableList<CookingStepWrapper> {
        val list = mutableListOf<CookingStepWrapper>()
        _wrappedSteps.value?.let { list.addAll(it) }
        return list
    }

    private fun getWrappedCircles(list: List<String>): List<CircleWrapper> {
        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            newList.add(circleWrapper)
        }
        return newList
    }

    private fun getWrappedSteps(list: List<CookingStep>): List<CookingStepWrapper> {
        val newList = mutableListOf<CookingStepWrapper>()
        for ((i, step) in list.withIndex()) {
            step.number = i + 1
            val wrappedStep = CookingStepWrapper(step, i)
            newList.add(wrappedStep)
        }
        return newList
    }

    private fun getWrappedItems(list: List<Item>): List<ItemWrapper> {
        val newList = mutableListOf<ItemWrapper>()
        for ((i, item) in list.withIndex()) {
            val wrappedItem = ItemWrapper(item.copy(), i)
            newList.add(wrappedItem)
        }
        return newList
    }

    private fun setupCircles(newCircles: List<String>) {
        circles.clear()
        circles.addAll(newCircles)
        _wrappedCircles.value = getWrappedCircles(newCircles)
    }

    private fun onRecipeLoaded(recipe: Recipe) {
        recipeTitle.value = recipe.title
        recipeCategory.value = recipe.category
        recipeCookingTime.value = recipe.cookingTime
        ingredients.addAll(JsonUtils.convertItemsFromJson(recipe.items))
        steps.addAll(JsonUtils.convertCookingStepsFromJson(recipe.cookingSteps))
        _wrappedIngredients.value = getWrappedItems(ingredients)
        _wrappedSteps.value = getWrappedSteps(steps)
    }

    private fun onDataNotAvailable() {
        TODO("Error while loading recipe")
    }


}

const val NEW_RECIPE_ID = 0L
const val NO_EDIT = -1