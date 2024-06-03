package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.taskHistory.HistoryTopBar
import it.polito.lab5.gui.taskHistory.TaskHistoryPage
import it.polito.lab5.viewModels.TaskHistoryViewModel

@Composable
fun TaskHistoryScreen(vm: TaskHistoryViewModel, navController: NavController) {
    Scaffold(
        topBar = { HistoryTopBar(navController = navController) },
    ) { paddingValues ->
        val users = vm.users.collectAsState().value

        TaskHistoryPage(
            history = vm.history,
            users = users,
            paddingValues = paddingValues
        )
    }
}