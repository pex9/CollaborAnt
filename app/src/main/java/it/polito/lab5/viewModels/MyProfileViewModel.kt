package it.polito.lab5.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel

class MyProfileViewModel(val model: MyModel, val auth: GoogleAuthentication) : ViewModel(){
    val users = model.users /*TODO questo dovrebbe richiamare il metodo getUser*/

    fun getUser(userId: String) = model.getUser(userId)

    var optionsOpened by mutableStateOf(false)
        private set
    fun setOptionsOpenedValue(b: Boolean) {
        optionsOpened = b
    }
}