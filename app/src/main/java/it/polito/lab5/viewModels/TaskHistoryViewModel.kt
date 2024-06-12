package it.polito.lab5.viewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

@RequiresApi(Build.VERSION_CODES.O)
class TaskHistoryViewModel(val taskId: String, val model: MyModel): ViewModel() {
    fun getTask(taskId: String) = model.getTask(taskId)

    fun getHistory(taskId: String) = model.getHistory(taskId)

    fun getTeam(teamId: String) = model.getTeam(teamId)

    fun getUsersTeam(members: List<String>) = model.getUsersTeam(members)
}