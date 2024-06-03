package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.lab5.gui.teamStats.HorizontalTeamStatsPane
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.gui.teamStats.VerticalTeamStatsPane
import it.polito.lab5.model.User
import it.polito.lab5.viewModels.TeamStatsViewModel

@Composable
fun TeamStatsScreen(
    vm: TeamStatsViewModel = viewModel(),
    navController: NavController,
    membersList: List<User>,
    teamId: Int,
) {
    val teams = vm.teams.collectAsState().value
    val team = teams.find { it.id == teamId }
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
                VerticalTeamStatsPane(
                    teams = teams,
                    tasks = tasks,
                    navController = navController,
                    p = paddingValues,
                    membersList = membersList,
                    teamId = teamId,
                )
            } else {
                HorizontalTeamStatsPane(
                    teams = teams,
                    tasks = tasks,
                    navController = navController,
                    p = paddingValues,
                    membersList = membersList,
                    teamId = teamId,
                )
            }
        }
    }
}