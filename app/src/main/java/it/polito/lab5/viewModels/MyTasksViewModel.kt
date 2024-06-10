package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.GoogleAuthentication

@RequiresApi(Build.VERSION_CODES.O)
class MyTasksViewModel(val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val users = model.users
    val teams = model.teams
    val tasks = model.tasks

    fun getUser(userId: String) = model.getUser(userId)

    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    fun getUserTasks(userId: String) = model.getUserTasks(userId)

    var categorySelectionOpened by mutableStateOf("")
        private set
    fun setCategorySelectionOpenedValue(s: String) {
        categorySelectionOpened = s
    }

    var categoryTaskListOpened by mutableStateOf("")
        private set
    fun setCategoryTaskListOpenedValue(s: String) {
        categoryTaskListOpened = s
    }

    var targetTaskId: String? by mutableStateOf(null)
        private set
    fun setTargetTaskIdValue(id: String?) { targetTaskId = id }

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

            } else{
                auth.getSignedInUserId()?.let { updateCategory(it, currentCategory, category) }

                // Update Category for all tasks of the user belonging to the previous category
                tasks.value.filter {
                    it.categories.containsKey(DataBase.LOGGED_IN_USER_ID) && it.categories[DataBase.LOGGED_IN_USER_ID] == currentCategory
                }.forEach { task ->
                    updateCategoryFromTask(task.id, DataBase.LOGGED_IN_USER_ID, category)
                }

                // Reset of currentCategory state
                currentCategory = ""
            }

            categorySelectionOpened = ""
            categoryTaskListOpened = ""

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

    private fun addCategoryToUser(userId: String, newCategory: String) = model.addCategoryToUser(userId, newCategory)

    private fun updateCategory(userId: String, oldCategory: String, newCategory: String) = model.updateCategory(userId, oldCategory, newCategory)

    fun updateCategoryFromTask(taskId: String, userId: String, newCategory: String) = model.updateCategoryFromTask(taskId,userId, newCategory)

    // check if the category has no tasks associated
    fun deleteCategoryFromUser(userId: String, category: String) = model.deleteCategoryFromUser(userId, category)

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