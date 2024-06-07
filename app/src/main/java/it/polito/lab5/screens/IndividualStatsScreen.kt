package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.individualStats.HorizontalIndividualStatsPane
import it.polito.lab5.gui.individualStats.VerticalIndividualStatsPane
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.viewModels.IndividualStatsViewModel

@Composable
fun IndividualStatsScreen(
    vm: IndividualStatsViewModel,
    navController: NavController
) {
    val kpiValues = vm.getUserKpi(vm.userId).collectAsState(initial = emptyList()).value
    val targetMember = vm.getUser(vm.userId).collectAsState(initial = null).value?.copy(kpiValues = kpiValues.toMap())
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value
    val membersList = team?.let {
        vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value.map { user ->
            val kpi = vm.getUserKpi(user.id).collectAsState(initial = emptyList()).value
            user.copy(kpiValues = kpi.toMap())
        }.sortedBy { user -> user.kpiValues[team.id]?.score }.reversed()
    }

    Scaffold(
        topBar = {
            if (team != null) {
                StatsTopBar(navController = navController, team = team)
            }
        },
    ) {paddingValues ->
        targetMember?.let {
            BoxWithConstraints {
                if(team != null && membersList != null) {


                    if (this.maxHeight > this.maxWidth) {
                        VerticalIndividualStatsPane(
                            team = team,
                            navController = navController,
                            p = paddingValues,
                            targetMember = it,
                            membersList = membersList
                        )

                    } else {
                        HorizontalIndividualStatsPane(
                            team = team,
                            navController = navController,
                            p = paddingValues,
                            targetMember = it,
                            membersList = membersList
                        )
                    }
                }
            }

        }
    }
}


