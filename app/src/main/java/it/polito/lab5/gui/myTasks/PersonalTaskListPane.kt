package it.polito.lab5.gui.myTasks

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.model.Task
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch

@Composable
fun PersonalTaskListPane(
    teams: List<Team>,
    categories: List<String>,
    tasks: List<Task>,
    loggedInUser: User,
    navController: NavController, // NavController for navigation
    p: PaddingValues, // Padding values for layout
    category: String,
    categoryError: String,
    resetCategoryError: () -> Unit,
    setCategoryValue: (String) -> Unit,
    isDialogOpen: Boolean,
    setIsDialogOpen: (Boolean) -> Unit,
    categorySelectionOpened: String,
    currentCategory: String,
    setCurrentCategory: (String) -> Unit,
    validate: suspend (User, List<Task>) -> Boolean,
    setCategorySelectionOpenedValue: (String) -> Unit,
    myTasksHideSheet: Boolean,
    setMyTasksHideSheet: (Boolean) -> Unit,
    updateUserCategoryToTask: suspend (Task, String, String) -> Unit,
    targetTask: Task?,
    setTargetTaskIdValue: (String) -> Unit,
    expandCategory: String,
    setExpandCategory: (String) -> Unit,
    isDialogDeleteOpen: Boolean,
    setIsDialogDeleteOpen: (Boolean) -> Unit,
    deleteCategoryFromUser: suspend (User, String) -> Unit,
    numberOfTasksForCategory: Int?,
    setNumberOfTasksForCategory: (Int?) -> Unit,
    errMsg: String,
    setErrMsgValue: (String) -> Unit,
    chosenCategory: String,
    setChosenCategoryValue: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if(isDialogOpen) {
        Dialog(
            onDismissRequest = { resetCategoryError() ; setIsDialogOpen(false) },
        ){
            val colors = MaterialTheme.colorScheme
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onBackground
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {

                    Text(
                        text = if(currentCategory.isNotBlank()) "Edit Category" else "New Category",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextFieldComp(
                        value = category,
                        updateValue = { setCategoryValue(it) },
                        errorMsg = categoryError,
                        label = "Category",
                        numLines = 1,
                        options = KeyboardOptions(),
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { resetCategoryError() ; setIsDialogOpen(false) }) {
                            Text(
                                text = "Cancel",
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = colors.onBackground
                            )
                        }

                        TextButton(
                            onClick = {
                                var isSuccess = false
                                scope.launch {
                                    isSuccess = validate(loggedInUser, tasks)
                                }.invokeOnCompletion {
                                    if(isSuccess) { setIsDialogOpen(false) }
                                }
                            }) {
                            Text(
                                text = if(currentCategory.isNotBlank()) "Save" else "Create",
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = colors.onBackground
                            )
                        }
                    }
                }
            }
        }
    }

    if(isDialogDeleteOpen) {
        DialogComp(
            title = "Confirm Delete",
            text = "Are you sure to delete \"${currentCategory}\" category?",
            onConfirmText = "Delete",
            onConfirm = {

                if(numberOfTasksForCategory == 0) {
                    scope.launch {
                        deleteCategoryFromUser(loggedInUser, currentCategory)
                    }.invokeOnCompletion { setIsDialogDeleteOpen(false) }
                } else {
                    setErrMsgValue("Cannot delete category which contains tasks!")
                    setIsDialogDeleteOpen(false)
                }
            },
            onDismiss = { setIsDialogDeleteOpen(false) }
        )
    }

    if(myTasksHideSheet) {
        MyTasksModalBottomSheet(
            targetTask = targetTask,
            categories = categories,
            loggedInUserId = loggedInUser.id,
            setMyTasksHideSheet = setMyTasksHideSheet,
            updateUserCategoryToTask = updateUserCategoryToTask,
            chosenCategory = chosenCategory,
            setChosenCategoryValue = setChosenCategoryValue,
        )
    }

    // Composable LazyColumn for displaying list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(p),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        // Display items for each category
        items(categories) { category ->
            // Display tasks for the current category
            CategoryItem(
                teams = teams,
                tasks = tasks,
                category = category,
                loggedInUserId = loggedInUser.id,
                navController = navController,
                setIsDialogOpen = setIsDialogOpen,
                setCurrentCategory = setCurrentCategory,
                setCategorySelectionOpenedValue = setCategorySelectionOpenedValue,
                categorySelectionOpened = categorySelectionOpened,
                setCategory = setCategoryValue,
                setMyTasksHideSheet = setMyTasksHideSheet,
                setTargetTaskIdValue = setTargetTaskIdValue,
                expandCategory = expandCategory,
                setExpandCategory = setExpandCategory,
                setIsDialogDeleteOpen = setIsDialogDeleteOpen,
                setNumberOfTasksForCategory = setNumberOfTasksForCategory,
                setChosenCategoryValue = setChosenCategoryValue
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }

    if(errMsg.isNotBlank()) {
        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
        setErrMsgValue("")
    }
}
