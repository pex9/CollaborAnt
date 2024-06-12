package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
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
import it.polito.lab5.LocalTheme
import it.polito.lab5.gui.myTasks.MyTasksTopBar
import it.polito.lab5.gui.myTasks.PersonalTaskListPane
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyTasksViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyTasksScreen (vm: MyTasksViewModel, navController: NavController) {
    val user = vm.auth.getSignedInUserId()?.let { vm.getUser(it).collectAsState(initial = null).value }
    val teams = user?.let { vm.getUserTeams(it.id).collectAsState(initial = emptyList()).value }
    val tasks = user?.let { vm.getUserTasks(it.id).collectAsState(initial = emptyList()).value }
    val colors = MaterialTheme.colorScheme

    Scaffold(
        topBar = {
            Column {
                MyTasksTopBar()
            }
         },
        bottomBar = { BottomNavigationBarComp(navController) },
        floatingActionButton = {
            val containerColor = if(LocalTheme.current.isDark) colors.secondary else colors.primary
            // Floating action button for adding a new team
            SmallFloatingActionButton(
                onClick = {
                    vm.setCurrentCategoryValue(""); vm.setCategoryValue(""); vm.setDialogOpenValue(true)
                }, // Navigate to add team screen on click
                shape = CircleShape,
                modifier = Modifier.size(60.dp),
                containerColor = containerColor, // Button color
            ) {
                // Icon for the floating action button
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Icon")
            }

        }
    ) { paddingValues ->
        val targetTask = tasks?.find { it.id == vm.targetTaskId }

        if (user != null && teams != null && tasks != null) {
            PersonalTaskListPane(
                teams = teams,
                categories = user.categories,
                tasks = tasks,
                loggedInUser = user,
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
                updateUserCategoryToTask = vm::updateUserCategoryToTask,
                targetTask = targetTask,
                setTargetTaskIdValue = vm::setTargetTaskIdValue,
                expandCategory = vm.categoryTaskListOpened,
                setExpandCategory = vm::setCategoryTaskListOpenedValue,
                isDialogDeleteOpen = vm.isDialogDeleteOpen,
                setIsDialogDeleteOpen = vm::setIsDialogDeleteOpen,
                deleteCategoryFromUser = vm::removeCategoryFromUser,
                numberOfTasksForCategory = vm.numberOfTasksForCategory,
                setNumberOfTasksForCategory = vm::setNumberOfTasksForCategoryValue,
                errMsg = vm.errMsg,
                setErrMsgValue = vm::setErrMsgValue,
                chosenCategory = vm.chosenCategory,
                setChosenCategoryValue = vm::setChosenCategoryValue
            )
        }
    }
}


