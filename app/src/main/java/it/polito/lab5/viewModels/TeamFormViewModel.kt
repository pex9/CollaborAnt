package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Empty
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.KPI
import it.polito.lab5.model.Message
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.model.calculateScore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TeamFormViewModel(private val currentTeamId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    val currentTeam = model.teams.value.find { it.id == currentTeamId }
    private var loggedInUser : User? = null
    init {
        val userid = auth.getSignedInUserId()
        if (userid != null) {
            viewModelScope.launch {
                loggedInUser = model.getUser(userid).first()
            }
        }
    }

    private suspend fun createTeam(team: Team) = model.createTeam(team)
    private suspend fun updateUser(userId: String, user: User) = model.updateUser(userId, user, false)
    private fun addTeam(team: Team): String = model.addTeam(team)
    private fun updateTeam(teamId: String, team: Team) = model.updateTeam(teamId, team)


    fun validate(): String {
        var id = ""

        checkName()
        checkDescription()

        if(nameError.isBlank() && descriptionError.isBlank()) {
            if(currentTeam == null) {
                id = addTeam(team = Team(
                    id = "",
                    image = image,
                    name = name,
                    description = description,
                    members = mapOf(DataBase.LOGGED_IN_USER_ID to Role.TEAM_MANAGER),
                    chat = emptyList()
                ))

            viewModelScope.launch {     //  TODO: add try/catch
                    loggedInUser?.let { user ->
                        val teamId = createTeam(team = Team(
                            id = "",
                            image = image,
                            name = name,
                            description = description,
                            members = mapOf(user.id to Role.TEAM_MANAGER),
                            chat = emptyList()
                        ))

                        val updatedKpiValues = user.kpiValues.toMutableMap()
                        updatedKpiValues[teamId] = KPI(
                            assignedTasks = 0,
                            completedTasks = 0,
                            score = calculateScore(0, 0)
                        )

                        updateUser(user.id,
                            user.copy(
                                joinedTeams = user.joinedTeams + 1,
                                kpiValues = updatedKpiValues
                            )
                        )
                    }
                }

            } else {
                currentTeamId?.let { id = it }
                updateTeam(currentTeam.id, currentTeam.copy(
                    name = name,
                    description = description,
                    image = image
                ))
            }
        }
        return id
    }

    var name by mutableStateOf(currentTeam?.name ?: "")
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

    var description by mutableStateOf(currentTeam?.description ?: "")
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

    var image by mutableStateOf(currentTeam?.image ?: Empty(pickRandomColor()))
        private set
    fun setImageValue(i: ImageProfile) {
        image = i
    }

    var showBottomSheet by mutableStateOf(false)
        private set
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }
}