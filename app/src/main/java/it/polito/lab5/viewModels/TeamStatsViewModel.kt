package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class TeamStatsViewModel(val teamId: String, val model: MyModel): ViewModel() {
    val teams = model.teams
    val tasks = model.tasks

    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUserKpi(userId: String) = model.getUserKpi(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)
}