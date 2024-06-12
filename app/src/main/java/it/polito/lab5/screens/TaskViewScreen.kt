package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.RepeatDialogComp
import it.polito.lab5.gui.taskView.CommentTextField
import it.polito.lab5.gui.taskView.MembersBottomSheet
import it.polito.lab5.gui.taskView.TaskPage
import it.polito.lab5.gui.taskView.TaskTopBar
import it.polito.lab5.model.Option
import it.polito.lab5.model.Role
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.viewModels.TaskViewViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskViewScreen(vm: TaskViewViewModel, navController: NavController) {
    val comments = vm.getTaskComments(vm.taskId).collectAsState(initial = emptyList()).value
    val attachments = vm.getAttachments(vm.taskId).collectAsState(initial = emptyList()).value
    val task = vm.getTask(vm.taskId).collectAsState(initial = null).value?.copy(
        comments = comments,
        attachments = attachments
    )
    val team= task?.teamId?.let { vm.getTeam(it).collectAsState(initial = null).value }
    val users = team?.members?.keys?.let { vm.getUsersTeam(it.toList()).collectAsState(initial = emptyList()).value }?.map {
        val kpiValues = vm.getUserKpi(it.id).collectAsState(initial = emptyList()).value
        it.copy(kpiValues = kpiValues.toMap())
    }
    val scope = rememberCoroutineScope()
    val isDelegatedMember = task?.teamMembers?.contains(vm.loggedInUserId)
    val loggedInUserRole = team?.members?.get(vm.loggedInUserId)

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            // Custom top bar for task details
            if(!vm.showDeleteLoading) {
                task?.let { task ->
                    if (loggedInUserRole != null && isDelegatedMember != null) {
                        TaskTopBar(
                            taskId = task.id,
                            repeat = task.repeat,
                            isDelegatedMember = isDelegatedMember,
                            loggedInUserRole = loggedInUserRole,
                            state = task.state,
                            updateState = {
                                scope.launch {
                                    if (users != null && vm.loggedInUserId != null) {
                                        vm.updateTaskState(
                                            task,
                                            users.filter { task.teamMembers.contains(it.id) },
                                            vm.loggedInUserId,
                                            it
                                        )
                                    }
                                }
                            },
                            optionsOpened = vm.optionsOpened,
                            setOptionsOpenedValue = vm::setOptionsOpenedValue,
                            showLoading = vm.showLoading,
                            stateSelOpened = vm.stateSelOpened,
                            setStateSelOpenedValue = vm::setStateSelOpenedValue,
                            setShowRepeatDeleteDialogValue = vm::setShowRepeatDeleteDialogValue,
                            setShowDeleteDialogValue = vm::setShowDeleteDialogValue,
                            navController = navController
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // BoxWithConstraints for responsive layout
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if(vm.showDeleteLoading) {
                CircularProgressIndicator(
                    color = CollaborantColors.DarkBlue,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                task?.let { task ->
                    if (isDelegatedMember != null && vm.loggedInUserId != null && loggedInUserRole != null && users != null) {
                        // TaskPage composable for displaying task details
                        TaskPage(
                            task = task,
                            users = users,
                            isDelegatedMember = isDelegatedMember,
                            loggedInUserId = vm.loggedInUserId,
                            loggedInUserRole = loggedInUserRole,
                            addAttachment = vm::addAttachmentToTask,
                            removeAttachment = vm::deleteAttachmentFromTask,
                            setShowBottomSheetValue = vm::setShowBottomSheetValue,
                            downloadFileFromFirebase = vm::downloadFileFromFirebase,
                            showLoading = vm.showLoading,
                            setShowLoadingValue = vm::setShowLoadingValue,
                            showDownloadLoading = vm.showDownloadLoading,
                            setShowDownloadLoadingValue = vm::setShowDownloadLoadingValue
                        )
                    }

                    if (isDelegatedMember == true || loggedInUserRole == Role.TEAM_MANAGER || loggedInUserRole == Role.SENIOR_MEMBER) {
                        // CommentTextField for adding comments
                        vm.loggedInUserId?.let {
                            CommentTextField(
                                isHorizontal = this.maxWidth > this.maxHeight,
                                value = vm.comment,
                                updateValue = vm::setCommentValue,
                                taskId = task.id,
                                addComment = vm::addCommentToTask,
                                loggedInUserId = it,
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }
                    }

                    // Bottom sheet for displaying task members
                    if (vm.showBottomSheet && team != null && users != null && vm.loggedInUserId != null) {
                        MembersBottomSheet(
                            team = team,
                            users = users,
                            members = task.teamMembers,
                            loggedInUserId = vm.loggedInUserId,
                            setShowBottomSheetValue = vm::setShowBottomSheetValue,
                            navController = navController
                        )
                    }

                    if (vm.showDeleteDialog) {
                        DialogComp(
                            title = "Confirm Delete",
                            text = "Are you sure to delete this task?",
                            onConfirmText = "Delete",
                            onConfirm = {
                                var deleteSuccess = false

                                scope.launch {
                                    if (users != null) {
                                        vm.setShowDeleteLoadingValue(true)
                                        deleteSuccess = vm.deleteTask(
                                            task = task,
                                            delegatedMembers = users.filter {
                                                task.teamMembers.contains(
                                                    it.id
                                                )
                                            },
                                            option = Option.CURRENT
                                        )
                                    }
                                }.invokeOnCompletion {
                                    vm.setShowDeleteLoadingValue(false)
                                    if (deleteSuccess) {
                                        vm.setShowDeleteDialogValue(false)
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onDismiss = { vm.setShowDeleteDialogValue(false) }
                        )
                    }

                    if (vm.showRepeatDeleteDialog) {
                        RepeatDialogComp(
                            title = "Confirm Delete",
                            text = "Are you sure to delete this recurrent task?",
                            onConfirmText = "Delete",
                            optionSelected = vm.optionSelected,
                            setOptionSelectedValue = vm::setOptionSelectedValue,
                            onConfirm = {
                                var deleteSuccess = false

                                scope.launch {
                                    if (users != null) {
                                        vm.setShowDeleteLoadingValue(true)
                                        deleteSuccess = vm.deleteTask(
                                            task = task,
                                            delegatedMembers = users.filter {
                                                task.teamMembers.contains(
                                                    it.id
                                                )
                                            },
                                            option = vm.optionSelected
                                        )
                                    }
                                }.invokeOnCompletion {
                                    vm.setShowDeleteLoadingValue(false)
                                    if (deleteSuccess) {
                                        vm.setOptionSelectedValue(Option.CURRENT)
                                        vm.setShowRepeatDeleteDialogValue(false)
                                        navController.popBackStack()
                                    }
                                }
                            },
                            onDismiss = {
                                vm.setOptionSelectedValue(Option.CURRENT)
                                vm.setShowRepeatDeleteDialogValue(false)
                            }
                        )
                    }
                }
            }
        }
    }
}