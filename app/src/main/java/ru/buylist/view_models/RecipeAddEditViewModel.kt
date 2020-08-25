package ru.buylist.view_models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.buylist.data.entity.Category
import ru.buylist.data.entity.CookingStep
import ru.buylist.data.entity.Item
import ru.buylist.data.entity.Recipe
import ru.buylist.data.entity.wrappers.CircleWrapper
import ru.buylist.data.entity.wrappers.CookingStepWrapper
import ru.buylist.data.entity.wrappers.ItemWrapper
import ru.buylist.data.repositories.recipe.RecipesDataSource
import ru.buylist.data.repositories.recipe.RecipesDataSource.*
import ru.buylist.utils.JsonUtils

class RecipeAddEditViewModel(
        private val repository: RecipesDataSource,
        private val recipeId: Long) : ViewModel() {

    var fabIsShown = ObservableBoolean(true)
    var prevArrowIsShown = ObservableBoolean(true)
    var nextArrowIsShown = ObservableBoolean(true)
    var itemName = ObservableField("")
    var step = ObservableField("")

    var isEditable: Boolean = false
    private var colorPosition = -1

    private lateinit var recipe: Recipe
    val items = mutableListOf<Item>()
    val cookingSteps = mutableListOf<CookingStep>()
    private val circles = mutableListOf<String>()

    val wrappedItems = MutableLiveData<List<ItemWrapper>>()
            .apply { value = emptyList() }
    val wrappedSteps = MutableLiveData<List<CookingStepWrapper>>()
            .apply { value = emptyList() }
    val wrappedCircles = MutableLiveData<List<CircleWrapper>>()
            .apply { value = emptyList() }


    init {
        loadList()
    }

    fun saveNewItem() {
        val title = itemName.get().toString().trim()
        if (title.isEmpty()) return

        val item = Item(name = title, category = getCategory())
        items.add(item)
        items.sortWith(compareBy({ it.category.color }, { it.id }))
        updateUi()
        itemName.set("")
        recipe.items = JsonUtils.convertItemsToJson(items)
        repository.updateRecipe(recipe)
    }

    fun saveNewStep() {
        val description = step.get().toString().trim()
        if (description.isEmpty()) return

        val newStep = CookingStep(cookingSteps.size + 1, description)
        cookingSteps.add(newStep)
        updateUi()
        step.set("")
        recipe.cookingSteps = JsonUtils.convertCookingStepsToJson(cookingSteps)
        repository.updateRecipe(recipe)
    }

    fun saveEditedItem(wrapper: ItemWrapper, newName: String) {
        val list = extractDataFromWrappedItems()
        wrapper.item.name = newName
        items[wrapper.position].name = newName

        updateWrappedItems(list, wrapper, false)
        recipe.items = JsonUtils.convertItemsToJson(items)
        repository.updateRecipe(recipe)
        isEditable = false
    }

    fun saveEditedStep(wrapper: CookingStepWrapper, newStep: String) {
        val list = extractDataFromWrappedSteps()
        wrapper.step.description = newStep
        cookingSteps[wrapper.position].description = newStep

        updateWrappedSteps(list, wrapper, false)
        recipe.cookingSteps = JsonUtils.convertCookingStepsToJson(cookingSteps)
        repository.updateRecipe(recipe)
        isEditable = false
    }

    fun editItem(wrapper: ItemWrapper) {
        val items = extractDataFromWrappedItems()
        checkEditableItemField(items)
        checkEditableStepField(extractDataFromWrappedSteps())
        updateWrappedItems(items, wrapper, true)
        isEditable = true
    }

    fun editStep(wrapper: CookingStepWrapper) {
        val steps = extractDataFromWrappedSteps()
        checkEditableStepField(steps)
        checkEditableItemField(extractDataFromWrappedItems())
        updateWrappedSteps(steps, wrapper, true)
        isEditable = true
    }

    fun deleteItem(wrapper: ItemWrapper) {
        // TODO: добавить возможность отмены удаления

        items.remove(wrapper.item)
        recipe.items = JsonUtils.convertItemsToJson(items)
        repository.updateRecipe(recipe)
        updateUi()
    }

    fun deleteStep(wrapper: CookingStepWrapper) {
        cookingSteps.remove(wrapper.step)
        recipe.cookingSteps = JsonUtils.convertCookingStepsToJson(cookingSteps)
        repository.updateRecipe(recipe)
        updateUi()
    }

    fun getCurrentColorPosition() = colorPosition

    fun saveCurrentColorPosition(position: Int) {
        colorPosition = position
    }

    fun updateCircle(selectedCircle: CircleWrapper) {
        val circleList: MutableList<CircleWrapper> = wrappedCircles.value as MutableList<CircleWrapper>
        checkSelectedCircles(circleList)
        selectedCircle.isSelected = !selectedCircle.isSelected
    }

    fun setupCircles(newCircles: List<String>) {
        circles.clear()
        circles.addAll(newCircles)
        wrappedCircles.value = getWrappedCircles(newCircles)
    }

    fun showHideArrows(prev: Boolean, next: Boolean) {
        prevArrowIsShown.set(prev)
        nextArrowIsShown.set(next)
    }

    fun showHideFab(dy: Int) {
        fabIsShown.set(dy <= 0)
    }

    private fun getCategory(): Category {
        return when {
            colorPosition < 0 -> Category()
            else -> Category(color = circles[colorPosition])
        }
    }

    private fun updateUi() {
        wrappedItems.value = getWrappedItems(items)
        wrappedSteps.value = getWrappedSteps(cookingSteps)
    }

    private fun updateWrappedSteps(list: MutableList<CookingStepWrapper>, wrapper: CookingStepWrapper,
                                   isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = wrapper.copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(wrapper.position)
        list.add(wrapper.position, newWrapper)
        wrappedSteps.value = list
    }

    private fun updateWrappedItems(list: MutableList<ItemWrapper>, wrapper: ItemWrapper,
                                   isEditable: Boolean = false, isSelected: Boolean = false) {
        val newWrapper = wrapper.copy(isEditable = isEditable, isSelected = isSelected)
        list.removeAt(wrapper.position)
        list.add(wrapper.position, newWrapper)
        wrappedItems.value = list
    }

    private fun checkEditableItemField(list: MutableList<ItemWrapper>) {
        if (isEditable) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isEditable) {
                    updateWrappedItems(list, item)
                    break
                }
            }
        }
    }

    private fun checkEditableStepField(list: MutableList<CookingStepWrapper>) {
        if (isEditable) {
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (item.isEditable) {
                    updateWrappedSteps(list, item)
                    break
                }
            }
        }
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
        wrappedItems.value?.let { list.addAll(it) }
        return list
    }

    private fun extractDataFromWrappedSteps(): MutableList<CookingStepWrapper> {
        val list = mutableListOf<CookingStepWrapper>()
        wrappedSteps.value?.let { list.addAll(it) }
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
            val wrappedStep = CookingStepWrapper(step.copy(), i)
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

    private fun loadList() {
        repository.getRecipe(recipeId, object : GetRecipeCallback {
            override fun onRecipeLoaded(loadedRecipe: Recipe) {
                items.clear()
                items.addAll(JsonUtils.convertItemsFromJson(loadedRecipe.items))
                wrappedItems.value = getWrappedItems(items)

                cookingSteps.clear()
                cookingSteps.addAll(JsonUtils.convertCookingStepsFromJson(loadedRecipe.cookingSteps))
                wrappedSteps.value = getWrappedSteps(cookingSteps)
                recipe = loadedRecipe
            }

            override fun onDataNotAvailable() {

            }

        })
    }


}