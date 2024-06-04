package it.polito.lab5.viewModels

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyProfileFormViewModel(val model: MyModel, private val auth: GoogleAuthentication) : ViewModel() {
    private val user = MutableStateFlow<User?>(null)

    private suspend fun updateUser(userid: String, user: User) = model.updateUser1(userid, user)

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
            user.value?.let { user ->

                val scope = viewModelScope

                scope.async {
                    updateUser(
                        user.id, user.copy(
                            first = firstNameValue,
                            last = lastNameValue,
                            nickname = nicknameValue,
                            email = emailValue,
                            location = locationValue,
                            description = descriptionValue,
                            telephone = telephoneValue,
                            imageProfile = imageProfile
                        )
                    )
                }.await()

                return true
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

    init {
        val userid = auth.getSignedInUserId()
        if (userid != null) {
            viewModelScope.launch {
                val u = model.getUser(userid).first()
                user.value = u
            }
        }

        // Observe user changes and update state
        viewModelScope.launch {
            user.collectLatest { newUser ->
                firstNameValue = newUser?.first ?: ""
                lastNameValue = newUser?.last ?: ""
            }
        }
    }


    var nicknameValue by mutableStateOf(user.value?.nickname?: "")
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

    var emailValue by mutableStateOf(user.value?.email?: "")
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

    var telephoneValue by mutableStateOf(user.value?.telephone?: "")
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

    var locationValue by mutableStateOf(user.value?.location?: "")
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

    var descriptionValue by mutableStateOf(user.value?.description?: "")
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

    var imageProfile: ImageProfile by mutableStateOf(user.value?.imageProfile?: Empty(pickRandomColor()))
        private set
    fun setImageProfileValue(i: ImageProfile) {
        imageProfile = i
    }

    var showBottomSheet by mutableStateOf(false)
        private set
    fun setShowBottomSheetValue(b: Boolean) {
        showBottomSheet = b
    }

}