package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class UserProfileViewModel(private val userId: Int, val model: MyModel): ViewModel() {
    val user = model.users.value.find { it.id == userId }
}