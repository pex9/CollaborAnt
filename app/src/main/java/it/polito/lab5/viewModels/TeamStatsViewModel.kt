package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.User

class TeamStatsViewModel(val teamId: String, val model: MyModel): ViewModel() {
    val teams = model.teams
    val tasks = model.tasks

    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)
}