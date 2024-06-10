package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.polito.lab5.gui.taskForm.TaskFormPage
import it.polito.lab5.gui.taskForm.TaskFormTopBar
import it.polito.lab5.viewModels.TaskFormViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskFormScreen(vm: TaskFormViewModel, navController: NavController) {
    val team = (vm.currentTask?.teamId ?: vm.teamId)?.let { vm.getTeam(it).collectAsState(initial = null).value }
    val users = team?.let { vm.getUsersTeam(team.members.keys.toList()).collectAsState(initial = emptyList()).value }?.map { user ->
        val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
        user.copy(kpiValues = kpi.toMap())
    }

    Scaffold(
        topBar = {
            if (team != null && users != null) {
                TaskFormTopBar(
                    taskId = vm.currentTask?.id,
                    navController = navController,
                    validate = vm::validate,
                    resetErrorMsg = vm::resetErrorMsg,
                    showLoading = vm.showLoading
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (team != null && users != null && vm.loggedInUser != null) {
                TaskFormPage(
                    team = team,
                    users = users,
                    loggedInUserId = vm.loggedInUser!!.id,
                    title = vm.title,
                    setTitleValue = vm::setTitleValue,
                    titleError = vm.titleError,
                    description = vm.description,
                    setDescriptionValue = vm::setDescriptionValue,
                    descriptionError = vm.descriptionError,
                    tag = vm.tag,
                    setTagValue = vm::setTagValue,
                    dueDate = vm.dueDate,
                    setDueDateValue = vm::setDueDateValue,
                    dueDateError = vm.dueDateError,
                    delegatedMembers = vm.delegatedMembers,
                    addMember = vm::addMember,
                    removeMember = vm::removeMember,
                    delegatedMembersError = vm.delegatedMembersError,
                    repeat = vm.repeat,
                    setEndRepeatDateValue = vm::setEndRepeatDateValue,
                    endRepeatDateError = vm.endRepeatDateError,
                    endRepeatDate = vm.endRepeatDate,
                    setRepeatValue = vm::setRepeatValue,
                    showTagMenu = vm.showTagMenu,
                    setShowTagMenuValue = vm::setShowTagMenuValue,
                    showRepeatMenu = vm.showRepeatMenu,
                    setShowRepeatMenuValue = vm::setShowRepeatMenuValue,
                    showDueDateDialog = vm.showDueDateDialog,
                    setShowEndRepeatDateDialogValue = vm::setShowEndRepeatDateDialogValue,
                    showEndRepeatDateDialog = vm.showEndRepeatDateDialog,
                    setShowDueDateDialogValue = vm::setShowDueDateDialogValue,
                    showEndRepeatField = vm.showEndRepeatField,
                    showMemberBottomSheet = vm.showMemberBottomSheet,
                    setShowMemberBottomSheetValue = vm::setShowMemberBottomSheetValue,
                    resetErrorMsg = vm::resetErrorMsg,
                    triState = vm.triState,
                    setTriStateValue = vm::setTriStateValue,
                    toggleTriState = vm::toggleTriState
                )
            }
        }
    }
}