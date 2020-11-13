package ru.buylist.presentation.recipe_add_edit

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.buylist.R
import ru.buylist.data.Result.Success
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.utils.CategoryInfo
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
    val quantity = MutableLiveData<String>()
    val unit = MutableLiveData<String>()
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
    private val _colors = MutableLiveData<List<String>>()
    private val _selectedColor = MutableLiveData<Int>()

    private val _circlesUpdate
            = MediatorLiveData<Pair<List<String>?, Int?>>().apply {
        addSource(_colors) { value = Pair(it, _selectedColor.value) }
        addSource(_selectedColor) { value = Pair(_colors.value, it) }
    }

    private val _circles: LiveData<List<CircleWrapper>> = _circlesUpdate.map { pair ->
        getWrappedCircles(pair.first, pair.second)
    }

    val circles: LiveData<List<CircleWrapper>> = _circles

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    // Event that opens the recipe detail screen
    private val _detailsEvent = MutableLiveData<Event<Recipe>>()
    val detailsEvent: LiveData<Event<Recipe>> = _detailsEvent


    fun start(recipeId: Long, newColors: List<String>) {
        _recipeId = recipeId
        _colors.value = newColors
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

    fun hideNewIngredientLayout() {
        _selectedColor.value = null
    }

    fun saveRecipe() {
        val recipe = Recipe()

        val title = recipeTitle.value
        if (title == null) {
            showSnackbarMessage(R.string.snackbar_empty_recipe_title)
            return
        }

        recipe.title = title

        val category = recipeCategory.value.toString().trim()
        if (category.isNotEmpty() && recipeCategory.value != null) {
            recipe.category = category
        }

        val time = recipeCookingTime.value.toString().trim()
        if (time.isNotEmpty() && recipeCookingTime.value != null) {
            recipe.cookingTime = time
        }

        if (ingredients.isNotEmpty()) {
            recipe.items = JsonUtils.convertItemsToJson(ingredients)
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

        val item = Item(name = name, color = getColor())

        val quantity = this.quantity.value.toString().trim()
        val unit = this.unit.value.toString().trim()

        if (this.quantity.value == null || quantity.isEmpty()) {
            // если количество не указано, то и ЕИ не сохраняем
        } else {
            item.quantity = quantity
            if (unit.isNotEmpty() || this.unit.value != null) {
                item.unit = unit
            }
        }

        ingredients.add(item)
        ingredients.sortWith(compareBy({ it.color }, { it.id }))
        updateUi()
        itemName.value = null
        this.quantity.value = null
        this.unit.value = null
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

    fun saveEditedItem(wrapper: ItemWrapper, newName: String, newQuantity: String, newUnit: String) {
        val list = extractDataFromWrappedItems()
        list[wrapper.position].item.apply {
            name = if (newName.trim().isEmpty()) this.name else newName
            quantity = newQuantity
            unit = if (newQuantity.isEmpty()) EMPTY else newUnit
        }
        ingredients[wrapper.position].apply {
            name = if (newName.trim().isEmpty()) this.name else newName
            quantity = newQuantity
            unit = if (newQuantity.isEmpty()) EMPTY else newUnit
        }
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
        ingredients.remove(wrapper.item)
        _wrappedIngredients.value = getWrappedItems(ingredients)
    }

    fun deleteStep(wrapper: CookingStepWrapper) {
        steps.removeAt(wrapper.position)
        _wrappedSteps.value = getWrappedSteps(steps)
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        _selectedColor.value = selectedCircle.position
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

    private fun getColor(): String {
        _selectedColor.value?.let {position ->
            _colors.value?.let { colors ->
                return colors[position]
            }
        } ?: return CategoryInfo.COLOR
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

    private fun getWrappedCircles(list: List<String>?, position: Int?): List<CircleWrapper> {
        if (list == null) return emptyList()

        val newList = mutableListOf<CircleWrapper>()
        for ((i, circle) in list.withIndex()) {
            val circleWrapper = CircleWrapper(circle, i)
            if (position != null && position == i) {
                circleWrapper.isSelected = true
            }
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

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
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
        showSnackbarMessage(R.string.snackbar_recipe_loading_error)
    }


}

const val NEW_RECIPE_ID = 0L
const val NO_EDIT = -1
const val EMPTY = ""