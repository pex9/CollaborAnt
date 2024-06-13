package it.polito.lab5.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.polito.lab5.gui.teamInfo.TeamInfoPage
import it.polito.lab5.gui.teamInfo.TeamInfoTopBar
import it.polito.lab5.viewModels.TeamInfoViewModel

@Composable
fun TeamInfoViewScreen(vm: TeamInfoViewModel, navController: NavController) {
    val scope = rememberCoroutineScope()
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value
    val users = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }?.map { user ->
        val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
        user.copy(kpiValues = kpi.toMap())
    }
    val loggedInUserRole =  team?.members?.get(vm.loggedInUserId)

    Scaffold(
        topBar = {
            if(!vm.showDeleteLoading) {
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
        }
    ) { paddingValues ->
        val colors = MaterialTheme.colorScheme
        if (vm.showDeleteLoading) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CircularProgressIndicator(color = colors.onBackground)
            }
        } else {
            team?.let {
                if (loggedInUserRole != null && vm.loggedInUserId != null && users != null) {
                    TeamInfoPage(
                        team = it,
                        users = users,
                        scope = scope,
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
                        setShowDeleteLoadingValue = vm::setShowDeleteLoadingValue,
                        resetUnreadMessage = vm::resetUnreadMessage,
                        paddingValues = paddingValues,
                        navController = navController
                    )
                }
            }
        }
    }
}