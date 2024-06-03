package it.polito.lab5.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import it.polito.lab5.viewModels.MyProfileFormViewModel
import it.polito.lab5.gui.myProfileForm.MyProfileFormTopBar
import it.polito.lab5.gui.myProfileForm.MyProfileFormPage

@Composable
fun MyProfileFormScreen(
    vm: MyProfileFormViewModel,
    navController: NavController,
    cameraContract: ManagedActivityResultLauncher<Intent, ActivityResult>,
    galleryContract: ManagedActivityResultLauncher<String, Uri?>,
) {
    Scaffold(
        topBar = { MyProfileFormTopBar(vm::validate, navController) },
    ){ paddingValues ->
        MyProfileFormPage(
            first = vm.firstNameValue,
            setFirst = vm::setFirstName,
            firstError = vm.firstNameError,
            last = vm.lastNameValue,
            setLast = vm::setLastName,
            lastError = vm.lastNameError,
            nickname = vm.nicknameValue,
            setNickname = vm::setNickname,
            nicknameError = vm.nicknameError,
            email = vm.emailValue,
            setEmail = vm::setEmail,
            emailError = vm.emailError,
            telephone = vm.telephoneValue,
            setTelephone = vm::setTelephone,
            telephoneError = vm.telephoneError,
            location = vm.locationValue,
            setLocation = vm::setLocation,
            locationError = vm.locationError,
            description = vm.descriptionValue,
            setDescription = vm::setDescription,
            descriptionError = vm.descriptionError,
            imageProfile = vm.imageProfile,
            setImageValue = vm::setImageProfileValue,
            showBottomSheet = vm.showBottomSheet,
            setShowBottomSheetValue = vm::setShowBottomSheetValue,
            galleryContract = galleryContract,
            cameraContract = cameraContract,
            paddingValues = paddingValues
        )
    }
}

