package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.taskHistory.HistoryTopBar
import it.polito.lab5.gui.taskHistory.TaskHistoryPage
import it.polito.lab5.viewModels.TaskHistoryViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskHistoryScreen(vm: TaskHistoryViewModel, navController: NavController) {
    val history = vm.getHistory(vm.taskId).collectAsState(initial = emptyList()).value
    val task = vm.getTask(vm.taskId).collectAsState(initial = null).value?.copy(history = history)
    val team = task?.let { vm.getTeam(task.teamId).collectAsState(initial = null).value }
    val users = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }

    Scaffold(
        topBar = { HistoryTopBar(navController = navController) },
    ) { paddingValues ->
        if (users != null) {
            TaskHistoryPage(
                history = task.history,
                users = users,
                paddingValues = paddingValues
            )
        }
    }
}