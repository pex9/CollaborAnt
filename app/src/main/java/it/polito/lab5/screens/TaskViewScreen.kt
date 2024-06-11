package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.taskView.CommentTextField
import it.polito.lab5.gui.taskView.MembersBottomSheet
import it.polito.lab5.gui.taskView.TaskPage
import it.polito.lab5.gui.taskView.TaskTopBar
import it.polito.lab5.model.Option
import it.polito.lab5.model.Role
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
            task?.let { task ->
                if (loggedInUserRole != null && isDelegatedMember != null) {
                    TaskTopBar(
                        taskId = task.id,
                        isDelegatedMember = isDelegatedMember,
                        loggedInUserRole = loggedInUserRole,
                        state = task.state,
                        updateState = {
                            scope.launch {
                                if(users != null && vm.loggedInUserId != null) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            task?.let { task ->
                if (isDelegatedMember != null && vm.loggedInUserId != null && loggedInUserRole != null && users != null ) {
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

                if(isDelegatedMember == true || loggedInUserRole == Role.TEAM_MANAGER || loggedInUserRole == Role.SENIOR_MEMBER) {
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

                if(vm.showDeleteDialog) {
                    DialogComp(
                        title = "Confirm Delete",
                        text = "Are you sure to delete this task?",
                        onConfirmText = "Delete",
                        onConfirm = {
                            var deleteSuccess = false

                            scope.launch {
                                if (users != null) {
                                    deleteSuccess = vm.deleteTask(task = task, users.filter { task.teamMembers.contains(it.id) })
                                }
                            }.invokeOnCompletion {
                                if(deleteSuccess) {
                                    vm.setShowDeleteDialogValue(false)
                                    navController.popBackStack()
                                }
                            }
                        },
                        onDismiss = { vm.setShowDeleteDialogValue(false) }
                    )
                }
            }
        }
    }
}


//@Preview
//@Composable
//fun Test() {
//    var showDialog by remember { mutableStateOf(true) }
//    var optionSelected by remember { mutableStateOf(Option.NOT_SPECIFIED) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        if (showDialog) {
//            AlertDialog(
//                onDismissRequest = { showDialog = false },
//                title = { Text(text = "Confirm delete") },
//                text = {
//                    val options = Option.entries.drop(1)
//
//                    LazyColumn {
//                        item { Text(text = "Are sure") }
//
//                        items(options) { option ->
//                            val literalOption = when (option) {
//                                Option.CURRENT -> "This task"
//                                Option.ALL -> "All tasks"
//                                Option.AFTER -> "This task and all next"
//                                Option.NOT_SPECIFIED -> ""
//                            }
//
//                            ListItem(
//                                headlineContent = { Text(text = literalOption) },
//                                leadingContent = {
//                                    RadioButton(
//                                        selected = optionSelected == option,
//                                        onClick = null
//                                    )
//                                },
//                                modifier = Modifier.selectable(
//                                    selected = optionSelected == option,
//                                    onClick = { optionSelected = option }
//                                )
//                            )
//                        }
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showDialog = false }) {
//                        Text(text = "Cancel")
//                    }
//                },
//                confirmButton = {
//                    TextButton(onClick = { showDialog = false }) {
//                        Text(text = "Save")
//                    }
//                }
//            )
//        }
//    }
//}