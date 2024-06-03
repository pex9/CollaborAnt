package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.DataBase

class MyTasksViewModel(val model: MyModel): ViewModel() {
    val users = model.users
    val teams = model.teams
    val tasks = model.tasks
    var categorySelectionOpened: MutableList<Pair<String, Boolean>> = mutableStateListOf()
    fun setCategorySelectionOpenedValue(category: String, b: Boolean) {
        val idx = categorySelectionOpened.indexOfFirst { it.first == category }

        categorySelectionOpened[idx] = category to b
    }

    var categoryTaskListOpened: MutableList<Pair<String, Boolean>> = mutableStateListOf()
    fun setCategoryTaskListOpenedValue(category: String, b: Boolean) {
        val idx = categoryTaskListOpened.indexOfFirst { it.first == category }

        categoryTaskListOpened[idx] = category to b
    }

    init {
        users.value.find { it.id == DataBase.LOGGED_IN_USER_ID }?.categories?.forEach {
            categorySelectionOpened.add(it to false)
            categoryTaskListOpened.add(it to false)
        }
    }

    var targetTaskId: Int? by mutableStateOf(null)
        private set
    fun setTargetTaskIdValue(id: Int?) { targetTaskId = id }

    var myTasksHideSheet by mutableStateOf(false)
        private set
    fun setMyTasksHideSheetValue(b: Boolean){ myTasksHideSheet = b }

    var category by mutableStateOf("")
        private set
    fun setCategoryValue(value: String) { category = value }

    var currentCategory by mutableStateOf("")
        private set
    fun setCurrentCategoryValue(value: String) { currentCategory = value }

    var categoryError by mutableStateOf("")
        private set

    fun resetCategoryError() {
        categoryError = ""
    }

    var isDialogOpen by mutableStateOf(false)
        private set

    fun setDialogOpenValue(value: Boolean) {
        isDialogOpen = value
    }

    var isDialogDeleteOpen by mutableStateOf(false)
        private set

    fun setIsDialogDeleteOpen(value: Boolean) {
        isDialogDeleteOpen = value
    }

    var numberOfTasksForCategory: Int? by mutableStateOf(null)
        private set

    fun setnumberOfTasksForCategory(value: Int?) {
        numberOfTasksForCategory = value
    }

    fun validate(): Boolean {
        checkCategory()

        if (categoryError.isBlank()) {
            if (currentCategory.isBlank()){
                addCategoryToUser(DataBase.LOGGED_IN_USER_ID, category)
                categorySelectionOpened.add(Pair(category, false))
                categoryTaskListOpened.add(Pair(category, false))
            }
            else{
                val index = categorySelectionOpened.indexOfFirst { it.first == currentCategory }

                if(index != -1) {
                    categorySelectionOpened[index] = category to false
                    categoryTaskListOpened[index] = category to false
                    updateCategory(DataBase.LOGGED_IN_USER_ID, currentCategory, category)

                    // Update Category for all tasks of the user belonging to the previous category
                    tasks.value.filter {
                        it.categories.containsKey(DataBase.LOGGED_IN_USER_ID) && it.categories[DataBase.LOGGED_IN_USER_ID] == currentCategory
                    }.forEach { task ->
                        updateCategoryFromTask(task.id, DataBase.LOGGED_IN_USER_ID, category)
                    }

                    // Reset of currentCategory state
                    currentCategory = ""
                }
            }
            return true
        }

        return false
    }

    private fun checkCategory() {
        val userCategories = users.value.find { it.id == DataBase.LOGGED_IN_USER_ID }?.categories ?: emptyList()

        categoryError = if (category.isBlank())
            "Category cannot be blank"
        else if (category.length > 50)
            "Category must contain less than 50 characters"
        else if(userCategories.contains(category))
            "Category already exists"
        else
            ""
    }

    private fun addCategoryToUser(userId: Int, newCategory: String) = model.addCategoryToUser(userId, newCategory)

    private fun updateCategory(userId: Int, oldCategory: String, newCategory: String) = model.updateCategory(userId, oldCategory, newCategory)

    fun updateCategoryFromTask(taskId: Int, userId: Int, newCategory: String) = model.updateCategoryFromTask(taskId,userId, newCategory)

    // check if the category has no tasks associated
    fun deleteCategoryFromUser(userId: Int, category: String) = model.deleteCategoryFromUser(userId, category)

    var errMsg by mutableStateOf("")
        private set
    fun setErrMsgValue(s: String) {
        errMsg = s
    }

    var chosenCategory by mutableStateOf("")
        private set
    fun setChosenCategoryValue(s: String) {
        chosenCategory = s
    }

    var isVisible by mutableStateOf(true)
        private set
    fun setIsVisibleValue(b: Boolean) {
        isVisible = b
    }
}