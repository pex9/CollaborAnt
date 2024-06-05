package it.polito.lab5.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.MyModel

class MyChatsViewModel(val model: MyModel): ViewModel() {
    val teams = model.teams
    private val userTeams = teams.value.filter { team -> team.members.any {it.key == DataBase.LOGGED_IN_USER_ID}}

    var chatsReadState: MutableList<Pair<String, Boolean>> = mutableStateListOf()
    fun setChatsReadStateValue(teamId: String, b: Boolean) {
        val idx = chatsReadState.indexOfFirst { it.first == teamId }
        chatsReadState[idx] = teamId to b
    }

    init {
        userTeams.forEach {
            chatsReadState.add(it.id to false)
        }
    }
}