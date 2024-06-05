package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.User

class TeamStatsViewModel(val teamId: String, val model: MyModel): ViewModel() {
    val teams = model.teams
    val tasks = model.tasks
    private val users = model.users
    val rankedMembersList = computeRankedMembersList()

    private fun computeRankedMembersList(): List<User> {
        val teamMembersPairs = teams.value.find { it.id == teamId }?.members
        val teamMembers = users.value.filter { it.id in teamMembersPairs!!.toList().map { pair -> pair.first } }
        val orderedTeamMembers = teamMembers.sortedBy { user -> user.kpiValues[teamId]?.score ?: 0 }.reversed()

        return orderedTeamMembers
    }
}