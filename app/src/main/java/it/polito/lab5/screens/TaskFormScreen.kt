package it.polito.lab5.screens

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

@Composable
fun TaskFormScreen(vm: TaskFormViewModel, navController: NavController) {
    Scaffold(
        topBar = {
            TaskFormTopBar(
                taskId = vm.currentTask?.id,
                navController = navController,
                validate = vm::validate,
                resetErrorMsg = vm::resetErrorMsg
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            val team = vm.teamId?.let { vm.getTeam(teamId = it).collectAsState(initial = null).value }
            val users = team?.members?.keys?.let { vm.getUsersTeam(it.toList()).collectAsState(initial = emptyList()).value }?.map { user ->
                val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
                user.copy(kpiValues = kpi.toMap())
            }

            if (team != null && users !=null) {
                TaskFormPage(
                    team = team,
                    users = users,
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
                    setRepeatValue = vm::setRepeatValue,
                    showTagMenu = vm.showTagMenu,
                    setShowTagMenuValue = vm::setShowTagMenuValue,
                    showRepeatMenu = vm.showRepeatMenu,
                    setShowRepeatMenuValue = vm::setShowRepeatMenuValue,
                    showDueDateDialog = vm.showDueDateDialog,
                    setShowDueDateDialogValue = vm::setShowDueDateDialogValue,
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