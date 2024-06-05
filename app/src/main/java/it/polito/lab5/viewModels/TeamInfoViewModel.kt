package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Role
import it.polito.lab5.model.User

class TeamInfoViewModel(val teamId: String, val model: MyModel): ViewModel() {
    val users = model.users
    val teams = model.teams

    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun deleteTeam(teamId: String) = model.deleteTeam(teamId)

    fun updateRole(teamId: String, memberId: String, role: Role) = model.updateRole(teamId, memberId, role)

    fun removeMember(teamId: String, memberId: String) = model.removeMember(teamId, memberId)

    var optionsOpened by mutableStateOf(false)
    fun setOptionsOpenedValue(b: Boolean) {
        optionsOpened = b
    }

    var roleSelectionOpened: MutableList<Pair<String, Boolean>> = mutableStateListOf()
    init {
        teams.value.find { it.id == teamId }?.let { team ->
            team.members.filter { it.value != Role.TEAM_MANAGER } }?.forEach {
                roleSelectionOpened.add(it.key to false)
        }
    }

    fun setRoleSelectionOpenedValue(memberId: String, b: Boolean) {
        val idx = roleSelectionOpened.indexOfFirst { it.first == memberId }

        roleSelectionOpened[idx] = memberId to b
    }

    var showMemberOptBottomSheet by mutableStateOf(false)
    fun setShowMemberOptBottomSheetValue(b: Boolean) {
        showMemberOptBottomSheet = b
    }

    var selectedUser: User? by mutableStateOf(null)
    fun setSelectedUserValue(u: User?) {
        selectedUser = u
    }

    var showLeaveDialog by mutableStateOf(false)
    fun setShowLeaveDialogValue(b: Boolean) {
        showLeaveDialog = b
    }

    var showDeleteDialog by mutableStateOf(false)
    fun setShowDeleteDialogValue(b: Boolean) {
        showDeleteDialog = b
    }

    var showMemberSelBottomSheet by mutableStateOf(false)
    fun setShowMemberSelBottomSheetValue(b: Boolean) {
        showMemberSelBottomSheet = b
    }

    var chosenMember: String? by mutableStateOf(null)
    fun setChosenMemberValue(u: String?) {
        chosenMember = u
    }

    var errorMsg by mutableStateOf("")
    fun setErrorMsgValue(e: String) {
        errorMsg = e
    }
}