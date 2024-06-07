package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamInfo.TeamInfoPage
import it.polito.lab5.gui.teamInfo.TeamInfoTopBar
import it.polito.lab5.viewModels.TeamInfoViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun TeamInfoViewScreen(vm: TeamInfoViewModel, scope: CoroutineScope, navController: NavController) {

    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value
    val users = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }?.map { user ->
        val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
        user.copy(kpiValues = kpi.toMap())
    }
    val loggedInUserRole =  team?.members?.get(vm.loggedInUserId)

    Scaffold(
        topBar = {
            team?.let {
                if (loggedInUserRole != null) {
                    TeamInfoTopBar(
                        team = it,
                        loggedInUserRole = loggedInUserRole,
                        navController = navController,
                        optionsOpened = vm.optionsOpened,
                        setOptionsOpenedValue = vm::setOptionsOpenedValue,
                        setShowMemberSelBottomSheetValue = vm::setShowMemberSelBottomSheetValue,
                        setShowLeaveDialogValue = vm::setShowLeaveDialogValue,
                        setShowDeleteDialogValue = vm::setShowDeleteDialogValue,
                    )
                }
            }
        }
    ) { paddingValues ->
        team?.let {
            if (loggedInUserRole != null && vm.loggedInUserId != null && users != null) {
                TeamInfoPage(
                    scope = scope,
                    team = it,
                    users = users,
                    loggedInUserId = vm.loggedInUserId,
                    loggedInUserRole = loggedInUserRole,
                    roleSelectionOpened = vm.roleSelectionOpened,
                    setRoleSelectionOpenedValue = vm::setRoleSelectionOpenedValue,
                    showMemberOptBottomSheet = vm.showMemberOptBottomSheet,
                    setShowMemberOptBottomSheetValue = vm::setShowMemberOptBottomSheetValue,
                    selectedUser = vm.selectedUser,
                    setSelectedUserValue = vm::setSelectedUserValue,
                    deleteTeam = vm::deleteTeam,
                    updateUserRole = vm::updateUserRole,
                    removeUserFromTeam = vm::removeUserFromTeam,
                    showLeaveDialog = vm.showLeaveDialog,
                    setShowLeaveDialogValue = vm::setShowLeaveDialogValue,
                    showDeleteDialog = vm.showDeleteDialog,
                    setShowDeleteDialogValue = vm::setShowDeleteDialogValue,
                    showMemberSelBottomSheet = vm.showMemberSelBottomSheet,
                    setShowMemberSelBottomSheetValue = vm::setShowMemberSelBottomSheetValue,
                    chosenMember = vm.chosenMember,
                    setChosenMemberValue = vm::setChosenMemberValue,
                    errorMsg = vm.errorMsg,
                    setErrorMsgValue = vm::setErrorMsgValue,
                    paddingValues = paddingValues,
                    navController = navController
                )
            }
        }
    }
}