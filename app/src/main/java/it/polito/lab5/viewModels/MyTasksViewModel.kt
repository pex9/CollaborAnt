package it.polito.lab5.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.Task
import it.polito.lab5.model.User
import kotlinx.coroutines.async

@RequiresApi(Build.VERSION_CODES.O)
class MyTasksViewModel(val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val users = model.users
    val teams = model.teams
    val tasks = model.tasks

    fun getUser(userId: String) = model.getUser(userId)

    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    fun getUserTasks(userId: String) = model.getUserTasks(userId)

    private suspend fun addCategoryToUser(user: User, newCategory: String) = model.addCategoryToUser(user, newCategory)

    private suspend fun updateCategoryToUser(user: User, oldCategory: String, newCategory: String) = model.updateCategoryToUser(user, oldCategory, newCategory)

    suspend fun updateUserCategoryToTask(task: Task, userId: String, newCategory: String) = model.updateUserCategoryToTask(task, userId, newCategory)

    suspend fun removeCategoryFromUser(user: User, category: String) = model.removeCategoryFromUser(user, category)

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

    fun setNumberOfTasksForCategoryValue(value: Int?) {
        numberOfTasksForCategory = value
    }

    suspend fun validate(loggedInUser: User, userTasks: List<Task>): Boolean {
        checkCategory(loggedInUser)

        if (categoryError.isBlank()) {
            try {
                viewModelScope.async {
                    if (currentCategory.isBlank()) {
                        addCategoryToUser(loggedInUser, category)
                    } else {
                        updateCategoryToUser(loggedInUser, currentCategory, category)

                        // Update Category for all tasks of the user belonging to the previous category
                        userTasks.filter { it.categories[loggedInUser.id] == currentCategory }.forEach { task ->
                            updateUserCategoryToTask(task, loggedInUser.id, category)
                        }
                    }
                }.await()

                currentCategory = ""
                categorySelectionOpened = ""
                categoryTaskListOpened = ""

                return true
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
                //showLoading = false
                return false
            }
        }
        return false
    }

    private fun checkCategory(loggedInUser: User) {
        val userCategories = loggedInUser.categories

        categoryError = if (category.isBlank())
            "Category cannot be blank"
        else if (category.length > 50)
            "Category must contain less than 50 characters"
        else if(userCategories.contains(category))
            "Category already exists"
        else
            ""
    }

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