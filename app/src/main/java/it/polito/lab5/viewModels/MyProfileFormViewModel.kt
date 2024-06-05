package it.polito.lab5.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.polito.lab5.model.Empty
import it.polito.lab5.model.GoogleAuthentication
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.MyModel
import it.polito.lab5.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyProfileFormViewModel(val model: MyModel, private val auth: GoogleAuthentication) : ViewModel() {
    private var user: User? = null

    private suspend fun updateUser(userid: String, user: User, deletePrevious: Boolean) = model.updateUser(userid, user, deletePrevious)

    suspend fun  validate() : Boolean {
        checkFirstName()
        checkLastName()
        checkNickname()
        checkEmail()
        checkLocation()
        checkDescription()
        checkTelephone()

        if(firstNameError.isBlank() && lastNameError.isBlank() && nicknameError.isBlank()
            && emailError.isBlank() && locationError.isBlank() && descriptionError.isBlank()
            && telephoneError.isBlank())
        {
            try {
                user?.let { user ->
                    viewModelScope.async {
                        showLoading = true
                        updateUser(
                            userid = user.id,
                            user = user.copy(
                                first = firstNameValue,
                                last = lastNameValue,
                                nickname = nicknameValue,
                                email = emailValue,
                                location = locationValue,
                                description = descriptionValue,
                                telephone = telephoneValue,
                                imageProfile = imageProfile
                            ),
                            deletePrevious = user.imageProfile !is Empty && imageProfile is Empty
                        )
                    }.await()

                    return true
                }
            } catch (e: Exception) {
                Log.e("Server Error", e.message.toString())
                showLoading = false
            }
        }
        return false
    }

    var firstNameValue by mutableStateOf("")
        private set
    var firstNameError by mutableStateOf("")
        private set
    fun setFirstName(f: String){
        firstNameValue = f
    }
    private fun checkFirstName() {
        firstNameError = if(firstNameValue.isBlank())
            "Firstname cannot be blank"
        else if(firstNameValue.length > 30)
            "Firstname must contain less than 30 characters"
        else
            ""
    }

    var lastNameValue by mutableStateOf("")
        private set
    var lastNameError by mutableStateOf("")
        private set
    fun setLastName(l: String) {
        lastNameValue = l
    }
    private fun checkLastName() {
        lastNameError = if(lastNameValue.isBlank())
            "Lastname cannot be blank"
        else if(lastNameValue.length > 30)
            "Lastname must contain less than 30 characters"
        else ""
    }

    var nicknameValue by mutableStateOf("")
        private set
    var nicknameError by mutableStateOf("")
        private set
    fun setNickname(n: String) {
        nicknameValue = n
    }
    private fun checkNickname() {
        nicknameError = if(nicknameValue.isBlank())
            "Nickname cannot be blank"
        else if(nicknameValue.length > 30)
            "Nickname must contain less than 30 characters"
        else
            ""
    }

    var emailValue by mutableStateOf("")
        private set
    var emailError by mutableStateOf("")
        private set
    fun setEmail(a: String) {
        emailValue = a
    }
    private fun checkEmail() {
        emailError = if(emailValue.isBlank())
            "Address cannot be blank"
        else if(!emailValue.contains("@"))
            "Invalid email address"
        else if(emailValue.length > 50)
            "Email must contain less than 50 characters"
        else
            ""
    }

    var telephoneValue by mutableStateOf("")
        private set
    var telephoneError by mutableStateOf("")
        private set
    fun setTelephone(t: String) {
        telephoneValue = t
    }
    private fun checkTelephone() {
        telephoneError = if(telephoneValue.isBlank())
            "Telephone cannot be blank"
        else if(!telephoneValue.isDigitsOnly())
            "Telephone must contain only numbers"
        else
            ""
    }

    var locationValue by mutableStateOf("")
        private set
    var locationError by mutableStateOf("")
        private set
    fun setLocation(l: String) {
        locationValue = l
    }
    private fun checkLocation() {
        locationError = if(locationValue.isBlank())
            "Location cannot be blank"
        else if(locationValue.length > 60)
            "Location must contain less than 60 characters"
        else
            ""
    }

    var descriptionValue by mutableStateOf("")
        private set
    var descriptionError by mutableStateOf("")
        private set
    fun setDescription(d: String) {
        descriptionValue = d
    }
    private fun checkDescription() {
        descriptionError = if(descriptionValue.isBlank())
            "Description cannot be blank"
        else if(descriptionValue.length > 250)
            "Description must contain less then 250 characters"
        else
            ""
    }

    var imageProfile: ImageProfile by mutableStateOf(Empty(pickRandomColor()))
        private set
    fun setImageProfileValue(i: ImageProfile) {
        imageProfile = i
    }

    var showBottomSheet by mutableStateOf(false)
        private set
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

    var showLoading by mutableStateOf(false)
        private set

    init {
        val userid = auth.getSignedInUserId()
        if (userid != null) {
            viewModelScope.launch {
                user = model.getUser(userid).first()
                firstNameValue = user?.first ?: ""
                lastNameValue = user?.last ?: ""
                nicknameValue = user?.nickname ?: ""
                emailValue = user?.email ?: ""
                telephoneValue = user?.telephone ?: ""
                locationValue = user?.location ?: ""
                descriptionValue = user?.description ?: ""
                imageProfile = user?.imageProfile ?: Empty(pickRandomColor())
            }
        }
    }

}