package it.polito.lab5.viewModels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MyTeamsViewModel(val teamId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    private var invitationTeam: Team? = null

    fun getUser(userId: String) = model.getUser(userId)

    fun getUserKpi(userId: String) = model.getUserKpi(userId)

    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    suspend fun addUserToTeam(team: Team, user: User): Boolean {
        try {
            viewModelScope.async {
                showLoading = true
                model.addUserToTeam(team, user)
            }.await()
            return true
        } catch (e: Exception) {
            Log.e("Server Error", e.message.toString())
            showLoading = false
            return false
        }
    }

    var showBottomSheet by mutableStateOf(teamId != null)
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

    var joinSuccess by mutableStateOf(false)
    fun setJoinSuccessValue(b: Boolean) {
        joinSuccess = b
    }

    var showLoading by mutableStateOf(false)
        private set
    fun setShowLoadingValue(b: Boolean) {
        showLoading = b
    }

    var animationState by mutableStateOf(true)
        private set

    fun setAnimationStateValue(b: Boolean) {
        animationState = b
    }

    init {
        val loggedInUserId = auth.getSignedInUserId()

        viewModelScope.launch {
            invitationTeam = teamId?.let { model.getTeam(it).first() }
            joinSuccess = invitationTeam?.members?.any { it.key == loggedInUserId } ?: false
        }
    }
}