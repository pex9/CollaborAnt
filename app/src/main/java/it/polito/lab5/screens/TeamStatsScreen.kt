package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamStats.HorizontalTeamStatsPane
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.gui.teamStats.VerticalTeamStatsPane
import it.polito.lab5.viewModels.TeamStatsViewModel

@Composable
fun TeamStatsScreen(
    vm: TeamStatsViewModel,
    navController: NavController,
    teamId: String,
) {


    val team = vm.getTeam(teamId).collectAsState(initial = null).value
    val membersList = team?.members?.keys?.let { vm.getUsersTeam(it.toList()).collectAsState(initial = emptyList()).value }
    Scaffold(
        topBar = {
            if (team != null) {
                StatsTopBar(navController = navController, team = team)
            }
        },
    ) {
        paddingValues ->
        BoxWithConstraints {

            val tasks = vm.tasks.collectAsState().value
            if (this.maxHeight > this.maxWidth) {
                if (team != null && membersList != null) {
                    VerticalTeamStatsPane(
                        team = team,
                        tasks = tasks,
                        navController = navController,
                        p = paddingValues,
                        membersList = membersList,
                    )
                }
            } else {
                if (team != null && membersList != null) {
                    HorizontalTeamStatsPane(
                        team = team,
                        tasks = tasks,
                        navController = navController,
                        p = paddingValues,
                        membersList = membersList,
                    )
                }
            }
        }
    }
}