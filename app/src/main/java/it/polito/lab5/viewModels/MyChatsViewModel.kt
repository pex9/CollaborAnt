package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.Team

@RequiresApi(Build.VERSION_CODES.O)
class MyChatsViewModel(val model: MyModel,  val auth: GoogleAuthentication): ViewModel() {
    fun getUserTeams(userId: String) = model.getUserTeams(userId)

    fun getTeamChat(teamId: String) = model.getTeamChat(teamId)

    suspend fun resetUnreadMessage(team: Team, userId: String) = model.updateUnreadMessage(team, listOf(userId), false)
}