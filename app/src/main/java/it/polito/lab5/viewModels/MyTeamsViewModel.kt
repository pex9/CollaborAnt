package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel

class MyTeamsViewModel(val teamId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val teams = model.teams

    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    val invitationTeam = teams.value.find { it.id == teamId }

    fun addMember(teamId: String, memberId: String) = model.addMember(teamId, memberId)

    var showBottomSheet by mutableStateOf(teamId != null)
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

    var joinSuccess by mutableStateOf(invitationTeam?.members?.any { it.key == DataBase.LOGGED_IN_USER_ID } ?: false )
    fun setJoinSuccessValue(b: Boolean) {
        joinSuccess = b
    }
}