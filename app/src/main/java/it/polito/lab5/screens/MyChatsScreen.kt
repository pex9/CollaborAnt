package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.myChats.MyChatsPage
import it.polito.lab5.gui.myChats.MyChatsTopBar
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyChatsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyChatsScreen(
    vm: MyChatsViewModel,
    navController: NavController
) {
    val loggedInUserId = vm.auth.getSignedInUserId()
    val teams = loggedInUserId?.let { vm.getUserTeams(it).collectAsState(initial = emptyList()).value }?.map {
            val chat = vm.getTeamChat(it.id).collectAsState(initial = emptyList()).value
            it.copy(chat = chat)
        }
    val chatsReadState = teams?.let { team -> team.map { it.id to (it.unreadMessage[loggedInUserId] ?: false) } }

    Scaffold(
        topBar = { MyChatsTopBar() },
        bottomBar = {
            BottomNavigationBarComp(navController)
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