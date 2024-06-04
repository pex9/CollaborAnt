package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import it.polito.lab5.gui.individualStats.HorizontalIndividualStatsPane
import it.polito.lab5.gui.individualStats.VerticalIndividualStatsPane
import it.polito.lab5.gui.teamStats.StatsTopBar
import it.polito.lab5.model.User
import it.polito.lab5.viewModels.IndividualStatsViewModel

@Composable
fun IndividualStatsScreen(
    vm: IndividualStatsViewModel = viewModel(),
    navController: NavController,
    targetMember: User?,
    targetMemberRanking: Int,
    membersList: List<User>,
    teamId: String,
) {
    val teams = vm.teams.collectAsState().value
    val team = teams.find { it.id == teamId }
    Scaffold(
        topBar = {
            if (team != null) {
                StatsTopBar(navController = navController, team = team)
            }
        },
    ) {paddingValues ->
        targetMember?.let {
            BoxWithConstraints {
                if (this.maxHeight > this.maxWidth) {
                    VerticalIndividualStatsPane(
                        vm = vm,
                        navController = navController,
                        p = paddingValues,
                        targetMember = it,
                        targetMemberRanking = targetMemberRanking,
                        membersList = membersList,
                        teamId = teamId,
                    )
                } else {
                    HorizontalIndividualStatsPane(
                        vm = vm,
                        navController = navController,
                        p = paddingValues,
                        targetMember = it,
                        targetMemberRanking = targetMemberRanking,
                        membersList = membersList,
                        teamId = teamId,
                    )
                }
            }

        }
    }
}


