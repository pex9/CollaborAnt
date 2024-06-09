package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Team

@RequiresApi(Build.VERSION_CODES.O)
class TeamViewModel(val teamId: String, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    fun getUserKpi(userId: String) = model.getUserKpi(userId)

    fun getTasksTeam(teamId: String) = model.getTasksTeam(teamId)

    fun getTaskComments(taskId: String) = model.getTaskComments(taskId)

    suspend fun resetUnreadMessage(team: Team, userId: String) = model.updateUnreadMessage(team, listOf(userId), false)

    val teams = model.teams
    val tasks = model.tasks
    val users = model.users

    var myTasksFilter = mutableStateOf(false)
    var priorityFilter: MutableState<Tag?> = mutableStateOf(null)
    var prioritySort: MutableState<String> = mutableStateOf("None")
    var dateSort: MutableState<String> = mutableStateOf("None")

    fun nextPriority(currentPriority: Tag?){
        when(currentPriority){
            Tag.HIGH -> priorityFilter.value = null
            Tag.MEDIUM -> priorityFilter.value = Tag.HIGH
            Tag.LOW -> priorityFilter.value = Tag.MEDIUM
            Tag.UNDEFINED -> priorityFilter.value = Tag.LOW
            null -> priorityFilter.value = Tag.UNDEFINED
        }
    }

    fun nextSortType(currentSortType: String, sortParameter: MutableState<String>){
        when(currentSortType){
            "Ascending" -> sortParameter.value = "Descending"
            "Descending" -> sortParameter.value = "None"
            "None" -> sortParameter.value = "Ascending"
        }
    }

    fun checkSortCondition(sortParameter: MutableState<String>){
        if(sortParameter== prioritySort && prioritySort.value != "None"){
            dateSort.value = "None"
        }
        else if(sortParameter== dateSort && dateSort.value != "None"){
            prioritySort.value = "None"
        }
    }

    var optionsOpened by mutableStateOf(false)

    fun setOptionsOpenedValue(b: Boolean) {
        optionsOpened = b
    }

    var showInfoText by mutableStateOf(true)
        private set
    fun setShowInfoTextValue(b: Boolean) {
        showInfoText = b
    }

    var filterState by mutableStateOf(false)
        private set
    fun setFilterStateValue(b: Boolean) {
        filterState = b
    }
}