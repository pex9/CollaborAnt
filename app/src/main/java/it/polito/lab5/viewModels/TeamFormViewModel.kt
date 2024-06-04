package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Empty
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team

class TeamFormViewModel(private val currentTeamId: String?, val model: MyModel): ViewModel() {
    val currentTeam = model.teams.value.find { it.id == currentTeamId }
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
                    members = listOf(DataBase.LOGGED_IN_USER_ID to Role.TEAM_MANAGER),
                    chat = emptyList()
                ))
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