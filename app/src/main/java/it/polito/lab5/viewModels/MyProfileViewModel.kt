package it.polito.lab5.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.MyModel

class MyProfileViewModel(val model: MyModel, val auth: GoogleAuthentication) : ViewModel(){
    val users = model.users

    fun getUser(userId: String) = model.getUser(userId)

    suspend fun deleteAccount() {
        val userId = auth.getSignedInUserId()

        try {
            Log.d("Utente", userId.toString())
            if (userId != null) { model.deleteUser(userId) }
            auth.deleteGoogleAccount()
        } catch (e: Exception) {
            Log.e("Server Error", e.toString())
        }
    }

    var optionsOpened by mutableStateOf(false)
        private set
    fun setOptionsOpenedValue(b: Boolean) {
        optionsOpened = b
    }

    var showDialog by mutableStateOf(false)
        private set
    fun setShowDialogValue(b: Boolean) {
        showDialog = b
    }
}