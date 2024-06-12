package it.polito.lab5.gui.individualStats

import it.polito.lab5.model.User

fun computeTargetMemberRanking(targetMemberId: String, members: List<User>): Int {
    val index = members.indexOfFirst { it.id == targetMemberId }
    return index + 1
}