package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.User

class IndividualStatsViewModel(val teamId: String, private val userId: String, val model: MyModel): ViewModel() {
    val teams = model.teams
    private val users = model.users
    val targetMember: User? = model.users.value.find { it.id == userId }
    val rankedMembersList = computeRankedMembersList()
    val targetMemberRanking = computeTargetMemberRanking()

    private fun computeRankedMembersList(): List<User> {
        val teamMembersPairs = teams.value.find { it.id == teamId }?.members
        val teamMembers = users.value.filter { it.id in teamMembersPairs!!.toList().map { pair -> pair.first } }
        val orderedTeamMembers = teamMembers.sortedBy { user -> user.kpiValues[teamId]?.score ?: 0 }.reversed()

        return orderedTeamMembers
    }

    private fun computeTargetMemberRanking(): Int {
        val index = rankedMembersList.indexOfFirst { it.id == userId }
        return index+1
    }
}