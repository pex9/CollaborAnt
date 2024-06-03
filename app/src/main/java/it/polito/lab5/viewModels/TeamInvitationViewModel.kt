package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class TeamInvitationViewModel(val teamId: Int, val model: MyModel): ViewModel() {
    val team = model.teams.value.find { it.id == teamId }
}