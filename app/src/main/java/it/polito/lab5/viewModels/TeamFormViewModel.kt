package it.polito.lab5.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.Empty
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.KPI
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.model.calculateScore
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TeamFormViewModel(val currentTeamId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    private var currentTeam: Team? = null
    private var loggedInUser : User? = null

    private fun getTeam(teamId: String) = model.getTeam(teamId)
    private suspend fun createTeam(team: Team) = model.createTeam(team)
    private suspend fun updateUser(userId: String, user: User) = model.updateUser(userId, user, false)
    private suspend fun updateTeam(teamId: String, team: Team, deletePrevious: Boolean) = model.updateTeam(teamId, team, deletePrevious)

    suspend fun validate(): String {
        var id = ""

        checkName()
        checkDescription()

        if(nameError.isBlank() && descriptionError.isBlank()) {
            try {
                viewModelScope.async {     //  TODO: add try/catch
                    if(currentTeam == null) {
                        loggedInUser?.let { user ->
                            id = createTeam(
                                team = Team(
                                    id = "",
                                    image = image,
                                    name = name,
                                    description = description,
                                    members = mapOf(user.id to Role.TEAM_MANAGER),
                                    chat = emptyList()
                                )
                            )

                            val updatedKpiValues = user.kpiValues.toMutableMap()
                            updatedKpiValues[id] = KPI(
                                assignedTasks = 0,
                                completedTasks = 0,
                                score = calculateScore(0, 0)
                            )

                            updateUser(
                                user.id,
                                user.copy(
                                    joinedTeams = user.joinedTeams + 1,
                                    kpiValues = updatedKpiValues
                                )
                            )
                        }
                    } else {
                        currentTeamId?.let {
                            id = it

                            updateTeam(     // TODO: fix update
                                teamId = currentTeamId,
                                team = currentTeam!!.copy(
                                    name = name,
                                    description = description,
                                    image = image
                                ),
                                deletePrevious = currentTeam!!.image !is Empty && image is Empty
                            )
                        }
                    }
                }.await()
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
                showLoading = false
            }
        }
        return id
    }

    var name by mutableStateOf("")
        private set
    var nameError by mutableStateOf("")
        private set
    fun setNameValue(n: String) {
        name = n
    }
    private fun checkName() {
        nameError = if (name.isBlank())
            "Title cannot be blank"
        else if (name.length > 50)
            "Title must contain less than 50 characters"
        else
            ""
    }

    var description by mutableStateOf("")
        private set
    var descriptionError by mutableStateOf("")
        private set
    fun setDescriptionValue(d: String) {
        description = d
    }
    private fun checkDescription() {
        descriptionError = if (description.length > 250)
            "Description must contain less than 250 characters"
        else
            ""
    }

    var image: ImageProfile by mutableStateOf(Empty(pickRandomColor()))
        private set
    fun setImageValue(i: ImageProfile) {
        image = i
    }

    var showBottomSheet by mutableStateOf(false)
        private set
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

    var showLoading by mutableStateOf(false)
        private set

    init {
        val userid = auth.getSignedInUserId()
        if (userid != null) {
            viewModelScope.launch {
                loggedInUser = model.getUser(userid).first()
                currentTeam = currentTeamId?.let { getTeam(it).first() }
                name = currentTeam?.name ?: ""
                description = currentTeam?.description ?: ""
                image = currentTeam?.image ?: Empty(pickRandomColor())
            }
        }
    }
}