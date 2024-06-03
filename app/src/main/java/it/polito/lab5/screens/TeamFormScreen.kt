package it.polito.lab5.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import it.polito.lab5.gui.teamForm.TeamFormTopBar
import it.polito.lab5.gui.teamForm.TeamFormPage
import it.polito.lab5.viewModels.TeamFormViewModel

@Composable
fun TeamFormScreen (
    vm: TeamFormViewModel,
    cameraContract: ManagedActivityResultLauncher<Intent, ActivityResult>,
    galleryContract: ManagedActivityResultLauncher<String, Uri?>,
    navController: NavController) {
    Scaffold(
        topBar = { TeamFormTopBar(validate = vm::validate, team = vm.currentTeam, navController = navController) },
    ) { paddingValues ->
        TeamFormPage(
            name = vm.name,
            setNameValue = vm::setNameValue,
            nameError = vm.nameError,
            description = vm.description,
            setDescriptionValue = vm::setDescriptionValue,
            descriptionError = vm.descriptionError,
            image = vm.image,
            setImageValue = vm::setImageValue,
            showBottomSheet = vm.showBottomSheet,
            setShowBottomSheetValue = vm::setShowBottomSheetValue,
            galleryContract = galleryContract,
            cameraContract = cameraContract,
            paddingValues = paddingValues
        )
    }
}