package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamStats.HorizontalTeamStatsPane
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.gui.teamStats.VerticalTeamStatsPane
import it.polito.lab5.viewModels.TeamStatsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamStatsScreen(
    vm: TeamStatsViewModel,
    navController: NavController
) {
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value
    val membersList = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }?.map { user ->
        val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
        user.copy(kpiValues = kpi.toMap())
    }
    val tasks = team?.let { vm.getTasksTeam(it.id).collectAsState(initial = emptyList()).value }

    Scaffold(
        topBar = {
            if (team != null) {
                StatsTopBar(navController = navController, team = team)
            }
        },
    ) {
        paddingValues ->
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                if (team != null && membersList != null && tasks != null) {
                    VerticalTeamStatsPane(
                        team = team,
                        tasks = tasks,
                        navController = navController,
                        p = paddingValues,
                        membersList = membersList,
                    )
                }
            } else {
                if (team != null && membersList != null && tasks != null) {
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