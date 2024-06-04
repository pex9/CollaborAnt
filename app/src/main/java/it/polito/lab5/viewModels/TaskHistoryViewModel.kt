package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class TaskHistoryViewModel(val taskId: String, val model: MyModel): ViewModel() {
    val users = model.users
    val history = model.tasks.value.find { it.id == taskId }?.history ?: emptyList()
}