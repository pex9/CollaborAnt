package it.polito.lab5.viewModels

import androidx.lifecycle.ViewModel
import it.polito.lab5.model.MyModel

class UserProfileViewModel(val userId: String, val model: MyModel): ViewModel() {
    fun getUser(userId: String) = model.getUser(userId)
}