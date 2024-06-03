package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.Message
import it.polito.lab5.model.MyModel

class ChatViewViewModel(val teamId: Int, userId: Int, val model: MyModel): ViewModel() {
    val teams = model.teams
    val users = model.users

    fun addMessage(teamId: Int, message: Message) = model.addMessage(teamId, message)

    var newMessage by mutableStateOf("")
        private set
    fun setNewMessageValue(c: String) {
        newMessage = c
    }

    var targetReceiver: Int? by mutableStateOf(if(userId != -1) userId else null)
        private set
    fun setTargetReceiverValue(r: Int?) {
        targetReceiver = r
    }



    var optionsOpened by mutableStateOf(false)
        private set
    fun setOptionsOpenedValue(state : Boolean){
        optionsOpened = state
    }
}