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
    navController: NavController, // NavController for navigation
) {
    val loggedInUserId = vm.auth.getSignedInUserId()
    val chat = vm.getTeamChat(vm.teamId).collectAsState(initial = emptyList()).value
    val team = vm.getTeam(vm.teamId).collectAsState(initial = null).value?.copy(chat = chat)
    val users = team?.let { vm.getUsersTeam(it.members.keys.toList()).collectAsState(initial = emptyList()).value }

    // Scaffold for layout structure
    Scaffold(
        topBar = {
            if (team != null) { TeamChatTopAppBar(team = team, navController = navController) }
        }, // Top app bar for task list
        bottomBar = {
            if (team != null && users != null) {
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
                        if (loggedInUserId != null) {
                            MessageTextField(
                                team = team,
                                loggedInUserId = loggedInUserId,
                                isHorizontal = this.maxWidth > this.maxHeight,
                                value = vm.newMessage,
                                updateValue = vm::setNewMessageValue,
                                addMessageToTeam = vm::addMessageToTeam,
                                newMessageReceiver = vm.targetReceiver,
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Content area
        if (team != null && users != null ) {
            TeamChatPage(
                team = team,
                users = users,
                paddingValues = paddingValues
            )
        }

    }
}