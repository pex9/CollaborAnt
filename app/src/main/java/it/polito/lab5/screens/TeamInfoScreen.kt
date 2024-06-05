package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamInfo.TeamInfoPage
import it.polito.lab5.gui.teamInfo.TeamInfoTopBar
import it.polito.lab5.model.DataBase
import it.polito.lab5.viewModels.TeamInfoViewModel

@Composable
fun TeamInfoViewScreen(vm: TeamInfoViewModel, navController: NavController) {
    val users = vm.users.collectAsState().value
    val team = vm.teams.collectAsState().value.find { it.id == vm.teamId }
    val loggedInUserRole =  team?.members?.get(DataBase.LOGGED_IN_USER_ID)

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
            if (loggedInUserRole != null) {
                TeamInfoPage(
                    team = it,
                    users = users,
                    loggedInUserRole = loggedInUserRole,
                    roleSelectionOpened = vm.roleSelectionOpened,
                    setRoleSelectionOpenedValue = vm::setRoleSelectionOpenedValue,
                    showMemberOptBottomSheet = vm.showMemberOptBottomSheet,
                    setShowMemberOptBottomSheetValue = vm::setShowMemberOptBottomSheetValue,
                    selectedUser = vm.selectedUser,
                    setSelectedUserValue = vm::setSelectedUserValue,
                    deleteTeam = vm::deleteTeam,
                    updateRole = vm::updateRole,
                    removeMember = vm::removeMember,
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