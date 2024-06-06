package it.polito.lab5.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class TeamInfoViewModel(val teamId: String, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val users = model.users
    val teams = model.teams

    val loggedInUserId = auth.getSignedInUserId()
    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    //TODO FIX DELETE TEAM
    suspend fun deleteTeam(team: Team, members: List<User>): Boolean {
        try {
            viewModelScope.async {
                model.deleteTeam(team, members)
            }.await()
            return true
        } catch (e: Exception) {
            Log.e("DeleteTeam", "Failed to delete team", e)
            return false
        }
    }

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