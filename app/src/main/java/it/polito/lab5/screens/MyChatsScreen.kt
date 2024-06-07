package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.myChats.MyChatsPage
import it.polito.lab5.gui.myChats.MyChatsTopBar
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyChatsViewModel

@Composable
fun MyChatsScreen(
    vm: MyChatsViewModel,
    navController: NavController
) {
    val loggedInUserId = vm.auth.getSignedInUserId()
    val teams = loggedInUserId?.let { vm.getUserTeams(it).collectAsState(initial = emptyList()).value }
    val chatsReadState = teams?.let { team -> team.map { it.id to (it.unreadMessage[loggedInUserId] ?: false) } }

    Scaffold(
        topBar = { MyChatsTopBar() },
        bottomBar = {
            if (chatsReadState != null) { BottomNavigationBarComp(navController, chatsReadState) }
        }
    ) { paddingValues ->
        if (teams != null && chatsReadState != null) {
            MyChatsPage(
                loggedInUserId = loggedInUserId,
                userTeams = teams,
                isReadState= chatsReadState,
                resetUnreadMessage = vm::resetUnreadMessage,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}