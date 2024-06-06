package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.lab5.gui.individualStats.HorizontalIndividualStatsPane
import it.polito.lab5.gui.individualStats.VerticalIndividualStatsPane
import it.polito.lab5.gui.individualStats.computeTargetMemberRanking
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.viewModels.IndividualStatsViewModel

@Composable
fun IndividualStatsScreen(
    vm: IndividualStatsViewModel = viewModel(),
    navController: NavController
) {
    val targetMember = vm.getUser(vm.userId).collectAsState(initial = null).value
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value
    val membersList = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }


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
                    val targetMemberRanking = computeTargetMemberRanking(targetMember.id, team.id, membersList)

                    if (this.maxHeight > this.maxWidth) {
                        VerticalIndividualStatsPane(
                            team = team,
                            navController = navController,
                            p = paddingValues,
                            targetMember = it,
                            targetMemberRanking = targetMemberRanking,
                            membersList = membersList
                        )

                    } else {
                        HorizontalIndividualStatsPane(
                            team = team,
                            navController = navController,
                            p = paddingValues,
                            targetMember = it,
                            targetMemberRanking = targetMemberRanking,
                            membersList = membersList
                        )
                    }
                }
            }

        }
    }
}


