package it.polito.lab5.screens

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.myChats.MyChatsPage
import it.polito.lab5.gui.myChats.MyChatsTopBar
import it.polito.lab5.model.DataBase
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyChatsViewModel

@Composable
fun MyChatsScreen(
    vm: MyChatsViewModel,
    navController: NavController
) {
    Scaffold(
        topBar = { MyChatsTopBar() },
        bottomBar = { BottomNavigationBarComp(navController, vm.chatsReadState)}
    ) { paddingValues ->
        val loggedInUserId = vm.auth.getSignedInUserId()
        val teams = loggedInUserId?.let { vm.getUserTeams(it).collectAsState(initial = emptyList()).value } //??NULL

        if (teams != null) {
            MyChatsPage(
                userTeams = teams,
                isReadState= vm.chatsReadState,
                setIsReadStateValue = vm::setChatsReadStateValue,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}