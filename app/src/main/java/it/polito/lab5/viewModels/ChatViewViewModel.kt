package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.Message
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Team

class ChatViewViewModel(val teamId: String, userId: String?, val model: MyModel, val auth: GoogleAuthentication): ViewModel() {
    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUser(userId: String) = model.getUser(userId)

    fun getUserKpi(userId: String) = model.getUserKpi(userId)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTeamChat(teamId: String) = model.getTeamChat(teamId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    suspend fun addMessageToTeam(team: Team, message: Message) = model.addMessageToTeam(team, message)

    suspend fun resetUnreadMessage(team: Team, userId: String) = model.updateUnreadMessage(team, listOf(userId), false)

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