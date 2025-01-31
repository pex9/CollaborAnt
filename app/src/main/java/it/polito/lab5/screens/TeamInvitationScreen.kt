package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamInvitation.TeamInvitationPage
import it.polito.lab5.gui.teamInvitation.TeamInvitationTopBar
import it.polito.lab5.viewModels.TeamInvitationViewModel

@Composable
fun TeamInvitationScreen(vm: TeamInvitationViewModel, navController: NavController) {
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value

    Scaffold(
        topBar = { TeamInvitationTopBar(navController = navController) },
    ) { paddingValues ->
        team?.let { TeamInvitationPage(team = it, paddingValues = paddingValues) }
    }
}