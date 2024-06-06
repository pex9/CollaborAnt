package it.polito.lab5.gui.individualStats

import it.polito.lab5.model.User

fun computeTargetMemberRanking(targetMemberId: String, teamId: String, members: List<User>): Int {
    val orderedTeamMembers = members.sortedBy { user -> user.kpiValues[teamId]?.score ?: 0 }.reversed()
    val index = orderedTeamMembers.indexOfFirst { it.id == targetMemberId }
    return index + 1
}