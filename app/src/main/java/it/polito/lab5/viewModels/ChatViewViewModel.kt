package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.Message
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Team

class ChatViewViewModel(val teamId: String, userId: String?, val model: MyModel): ViewModel() {
    val teams = model.teams
    val users = model.users

    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)
    suspend fun addMessageToTeam(team: Team, message: Message) = model.addMessageToTeam(team, message)

    fun addMessage(teamId: String, message: Message) = model.addMessage(teamId, message)

    var newMessage by mutableStateOf("")
        private set
    fun setNewMessageValue(c: String) {
        newMessage = c
    }

    var targetReceiver: String? by mutableStateOf(userId)
        private set
    fun setTargetReceiverValue(r: String?) {
        targetReceiver = r
    }

    var optionsOpened by mutableStateOf(false)
        private set
    fun setOptionsOpenedValue(state : Boolean){
        optionsOpened = state
    }
}