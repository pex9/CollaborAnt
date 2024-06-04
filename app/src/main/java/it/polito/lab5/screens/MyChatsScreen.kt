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
    isReadState: MutableList<Pair<String, Boolean>>,
    setIsReadStateValue: (String, Boolean) -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = { MyChatsTopBar() },
        bottomBar = { BottomNavigationBarComp(navController, isReadState)}
    ) { paddingValues ->
        val userTeams = vm.teams.collectAsState().value.filter {
            it.members.any{ member -> member.first == DataBase.LOGGED_IN_USER_ID }
        }

        MyChatsPage(
            userTeams = userTeams,
            isReadState = isReadState,
            setIsReadStateValue = setIsReadStateValue,
            navController = navController,
            paddingValues = paddingValues
        )
    }
}