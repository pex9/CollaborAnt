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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Task
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
fun PersonalTaskListPane(
    teams: List<Team>,
    categories: List<String>,
    tasks: List<Task>,
    navController: NavController, // NavController for navigation
    p: PaddingValues, // Padding values for layout
    category: String,
    categoryError: String,
    resetCategoryError: () -> Unit,
    setCategoryValue: (String) -> Unit,
    isDialogOpen: Boolean,
    setIsDialogOpen: (Boolean) -> Unit,
    categorySelectionOpened: MutableList<Pair<String, Boolean>>,
    currentCategory: String,
    setCurrentCategory: (String) -> Unit,
    validate: () -> Boolean,
    setCategorySelectionOpenedValue: (String, Boolean) -> Unit,
    myTasksHideSheet: Boolean,
    setMyTasksHideSheet: (Boolean) -> Unit,
    updateCategoryFromTask: ( Int, Int, String) -> Unit,
    taskId: Int?,
    setTargetTaskIdValue: (Int) -> Unit,
    expandCategory: MutableList<Pair<String, Boolean>>,
    setExpandCategory: (String, Boolean) -> Unit,
    isDialogDeleteOpen: Boolean,
    setIsDialogDeleteOpen: (Boolean) -> Unit,
    deleteCategoryFromUser: (Int,String) -> Unit,
    numberOfTasksForCategory: Int?,
    setNumberOfTasksForCategory: (Int?) -> Unit,
    errMsg: String,
    setErrMsgValue: (String) -> Unit,
    chosenCategory: String,
    setChosenCategoryValue: (String) -> Unit,
    setIsVisibleValue: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberLazyListState()

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemScrollOffset <= 0 }
            .collect { checkVisible ->
                setIsVisibleValue(checkVisible)
            }
    }

    if(isDialogOpen) {
        Dialog(
            onDismissRequest = { resetCategoryError() ; setIsDialogOpen(false) },
        ){
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
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
                        color = CollaborantColors.DarkBlue
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
                                color = Color.Black
                            )
                        }

                        TextButton(onClick = { if(validate()) { setIsDialogOpen(false) } }) {
                            Text(
                                text = if(currentCategory.isNotBlank()) "Save" else "Create",
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color.Black
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
                setIsDialogDeleteOpen(false)
                if(numberOfTasksForCategory == 0){
                    deleteCategoryFromUser(DataBase.LOGGED_IN_USER_ID, currentCategory)
                } else{ setErrMsgValue("Cannot delete category which contains tasks!") }
            },
            onDismiss = { setIsDialogDeleteOpen(false) }
        )
    }

    if(myTasksHideSheet) {
        MyTasksModalBottomSheet(
            taskId = taskId,
            categories = categories,
            setMyTasksHideSheet = setMyTasksHideSheet,
            updateCategoryFromTask = updateCategoryFromTask,
            chosenCategory = chosenCategory,
            setChosenCategoryValue = setChosenCategoryValue,
        )
    }

    // Composable LazyColumn for displaying list
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .fillMaxSize()
            .padding(p), // Apply padding
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        /*item {

        }*/

        // Display items for each category
        itemsIndexed(categories) { index, category ->
            // Display tasks for the current category
            CategoryItem(
                teams = teams,
                tasks = tasks,
                category = category,
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
    }

    if(errMsg.isNotBlank()) {
        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
        setErrMsgValue("")
    }
}
