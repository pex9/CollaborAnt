package it.polito.lab5.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel

class MyChatsViewModel(val model: MyModel,  val auth: GoogleAuthentication): ViewModel() {


    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    var chatsReadState: MutableList<Pair<String, Boolean>> = mutableStateListOf()
    fun setChatsReadStateValue(teamId: String, b: Boolean) {
        val idx = chatsReadState.indexOfFirst { it.first == teamId }
        chatsReadState[idx] = teamId to b
    }

    init {
        /*userTeams.forEach {
            chatsReadState.add(it.id to false)
        }*/
    }
}