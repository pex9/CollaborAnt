package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

@RequiresApi(Build.VERSION_CODES.O)
class TeamStatsViewModel(val teamId: String, val model: MyModel): ViewModel() {
    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUserKpi(userId: String) = model.getUserKpi(userId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)

    fun getTasksTeam(teamId: String) = model.getTasksTeam(teamId)
}