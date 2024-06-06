package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class IndividualStatsViewModel(val teamId: String, val userId: String, val model: MyModel): ViewModel() {
    fun getTeam(teamId: String) = model.getTeam(teamId)
    fun getUser(userId: String) = model.getUser(userId)
    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)
}