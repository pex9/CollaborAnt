package it.polito.lab5.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import it.polito.lab5.gui.login.LoginPage
import it.polito.lab5.model.MyApplication
import it.polito.lab5.viewModels.LogInViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(vm : LogInViewModel, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by vm.state.collectAsState()
    val auth = (context.applicationContext as MyApplication).auth
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                scope.launch {
                    val signInResult = auth.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    vm.onSignInResult(signInResult)
                }
            }
        }
    )

    LaunchedEffect(key1 = state) {
        if (state.isSignInSuccessful) {
            Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
            navController.navigate("myTeams?teamId={teamId}")
            vm.resetState()
        } else if(state.signInError != null) {
            vm.setShowLoadingValue(false)
            Toast.makeText(context, state.signInError, Toast.LENGTH_LONG).show()
        }
    }

    LoginPage(
        showLoading = vm.showLoading,
        onSignInClick = {
            vm.setShowLoadingValue(true)
            scope.launch {
                val signInIntentSender = auth.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(signInIntentSender ?: return@launch).build()
                )
            }
        }
    )
}