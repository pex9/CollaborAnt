package it.polito.lab5.viewModels

import androidx.compose.ui.graphics.Color
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors

fun pickRandomColor(): Color {
    val colorPool = listOf(
        CollaborantColors.LightBlue,
        CollaborantColors.MediumBlue,
        CollaborantColors.DarkBlue,
        CollaborantColors.CardBackGroundGray,
        CollaborantColors.Yellow
    )
    return colorPool.random()
}
private fun computeRankedMembersList(teamMembers : List<User>,teamId: String): List<User> {
    val orderedTeamMembers = teamMembers.sortedBy { user -> user.kpiValues[teamId]?.score ?: 0 }.reversed()

    return orderedTeamMembers
}