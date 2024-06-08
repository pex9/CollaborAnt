package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.taskView.CommentTextField
import it.polito.lab5.gui.taskView.MembersBottomSheet
import it.polito.lab5.gui.taskView.TaskPage
import it.polito.lab5.gui.taskView.TaskTopBar
import it.polito.lab5.model.Role
import it.polito.lab5.viewModels.TaskViewViewModel

@Composable
fun TaskViewScreen(vm: TaskViewViewModel, navController: NavController) {
    val task = vm.tasks.collectAsState().value.find { it.id == vm.taskId }
    val team= task?.teamId?.let { vm.getTeam(it).collectAsState(initial = null).value }
    val users = team?.members?.keys?.let { vm.getUsersTeam(it.toList()).collectAsState(initial = emptyList()).value }

    val isDelegatedMember = task?.teamMembers?.contains(vm.loggedInUserId)
    val loggedInUserRole = team?.members?.get(vm.loggedInUserId)

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            // Custom top bar for task details
            task?.let { task ->
                if (loggedInUserRole != null && isDelegatedMember != null) {
                    TaskTopBar(
                        taskId = task.id,
                        isDelegatedMember = isDelegatedMember,
                        loggedInUserRole = loggedInUserRole,
                        state = task.state,
                        updateState = { vm.setTaskState(task.id, it) },
                        optionsOpened = vm.optionsOpened,
                        setOptionsOpenedValue = vm::setOptionsOpenedValue,
                        stateSelOpened = vm.stateSelOpened,
                        setStateSelOpenedValue = vm::setStateSelOpenedValue,
                        setShowDeleteDialogValue = vm::setShowDeleteDialogValue,
                        navController = navController
                    )
                }
            }
        }
    ) { paddingValues ->
        // BoxWithConstraints for responsive layout
        BoxWithConstraints(
            modifier = Modifier.padding(paddingValues)
        ) {
            task?.let { task ->
                if (isDelegatedMember != null && loggedInUserRole != null && users != null) {
                    // TaskPage composable for displaying task details
                    TaskPage(
                        task = task,
                        users = users,
                        isDelegatedMember = isDelegatedMember,
                        loggedInUserRole = loggedInUserRole,
                        addAttachment = vm::addAttachment,
                        removeAttachment = vm::removeAttachment,
                        setShowBottomSheetValue = vm::setShowBottomSheetValue
                    )
                }

                if(isDelegatedMember == true || loggedInUserRole == Role.TEAM_MANAGER || loggedInUserRole == Role.SENIOR_MEMBER) {
                    // CommentTextField for adding comments
                    CommentTextField(
                        isHorizontal = this.maxWidth > this.maxHeight,
                        value = vm.comment,
                        updateValue = vm::setCommentValue,
                        taskId = task.id,
                        addComment = vm::addComment,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }

                // Bottom sheet for displaying task members
                if (vm.showBottomSheet && team != null && users != null) {
                        MembersBottomSheet(
                            team = team,
                            users = users,
                            members = task.teamMembers,
                            setShowBottomSheetValue = vm::setShowBottomSheetValue,
                            navController = navController
                        )
                }

                if(vm.showDeleteDialog) {
                    DialogComp(
                        title = "Confirm Delete",
                        text = "Are you sure to delete this task?",
                        onConfirmText = "Delete",
                        onConfirm = {
                            vm.setShowDeleteDialogValue(false)
                            vm.deleteTask(task.id)
                            navController.popBackStack()
                        },
                        onDismiss = { vm.setShowDeleteDialogValue(false) }
                    )
                }
            }
        }
    }
}