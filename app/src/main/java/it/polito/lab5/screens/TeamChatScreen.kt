package it.polito.lab5.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.teamChat.MessageTextField
import it.polito.lab5.gui.teamChat.ReceiverSelector
import it.polito.lab5.gui.teamChat.TeamChatPage
import it.polito.lab5.gui.teamChat.TeamChatTopAppBar
import it.polito.lab5.viewModels.ChatViewViewModel

@Composable
fun TeamChatScreen (
    vm: ChatViewViewModel,
    setIsReadState: (Int, Boolean) -> Unit,
    navController: NavController, // NavController for navigation
) {
    val team = vm.teams.collectAsState().value.find { it.id == vm.teamId }
    val users = vm.users.collectAsState().value

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            if (team != null) {
                TeamChatTopAppBar(
                    team = team,
                    navController = navController
                )
            }
        }, // Top app bar for task list
        bottomBar = {
            if (team != null) {
                Column {
                    ReceiverSelector(
                        team = team,
                        users = users,
                        optionsOpened = vm.optionsOpened,
                        setOptionsOpenedValue = vm::setOptionsOpenedValue,
                        targetReceiver = vm.targetReceiver,
                        setReceiverTargetValue = vm::setTargetReceiverValue
                    )

                    BoxWithConstraints {
                        MessageTextField(
                            isHorizontal = this.maxWidth > this.maxHeight,
                            value = vm.newMessage,
                            updateValue = vm::setNewMessageValue,
                            taskId = team.id,
                            addMessage = vm::addMessage,
                            newMessageReceiver = vm.targetReceiver,
                            setIsReadState = setIsReadState,
                            teamId = team.id
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        // Content area
        if (team != null) {
            TeamChatPage(
                team = team,
                users = users,
                paddingValues = paddingValues
            )
        }

    }
}