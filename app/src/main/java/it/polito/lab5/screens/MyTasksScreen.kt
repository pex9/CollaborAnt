package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.lab5.gui.myTasks.MyTasksTopBar
import it.polito.lab5.gui.myTasks.PersonalTaskListPane
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.TaskState
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyTasksViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyTasksScreen (vm: MyTasksViewModel, navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavigationBarComp(navController) },
        topBar = { MyTasksTopBar() },
        floatingActionButton = {
            if(vm.isVisible) {
                // Floating action button for adding a new team
                SmallFloatingActionButton(
                    onClick = {
                        vm.setCurrentCategoryValue(""); vm.setCategoryValue(""); vm.setDialogOpenValue(
                        true
                    )
                    }, // Navigate to add team screen on click
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp),
                    containerColor = MaterialTheme.colorScheme.primary, // Button color
                ) {
                    // Icon for the floating action button
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Icon")
                }
            }
        }
    ) { paddingValues ->
        val user = vm.users.collectAsState().value.first { it.id == DataBase.LOGGED_IN_USER_ID }

        PersonalTaskListPane(
            teams = vm.teams.collectAsState().value,
            categories = user.categories,
            tasks = vm.tasks.collectAsState().value.filter { it.teamMembers.contains(DataBase.LOGGED_IN_USER_ID) && it.state!= TaskState.COMPLETED },
            navController = navController,
            p = paddingValues,
            category = vm.category,
            categoryError = vm.categoryError,
            resetCategoryError = vm::resetCategoryError,
            setCategoryValue = vm::setCategoryValue,
            isDialogOpen = vm.isDialogOpen,
            setIsDialogOpen = vm::setDialogOpenValue,
            categorySelectionOpened = vm.categorySelectionOpened,
            currentCategory = vm.currentCategory,
            setCurrentCategory = vm::setCurrentCategoryValue,
            validate = vm::validate,
            setCategorySelectionOpenedValue = vm::setCategorySelectionOpenedValue,
            myTasksHideSheet = vm.myTasksHideSheet,
            setMyTasksHideSheet = vm::setMyTasksHideSheetValue,
            updateCategoryFromTask = vm::updateCategoryFromTask,
            taskId = vm.targetTaskId,
            setTargetTaskIdValue = vm::setTargetTaskIdValue,
            expandCategory = vm.categoryTaskListOpened,
            setExpandCategory = vm::setCategoryTaskListOpenedValue,
            isDialogDeleteOpen = vm.isDialogDeleteOpen,
            setIsDialogDeleteOpen = vm::setIsDialogDeleteOpen,
            deleteCategoryFromUser = vm::deleteCategoryFromUser,
            numberOfTasksForCategory = vm.numberOfTasksForCategory,
            setNumberOfTasksForCategory = vm::setnumberOfTasksForCategory,
            errMsg = vm.errMsg,
            setErrMsgValue = vm::setErrMsgValue,
            chosenCategory = vm.chosenCategory,
            setChosenCategoryValue = vm::setChosenCategoryValue,
            setIsVisibleValue = vm::setIsVisibleValue,
        )
    }
}


