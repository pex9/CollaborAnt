package it.polito.lab5.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import it.polito.lab5.gui.Login.SignInPage
import it.polito.lab5.model.SignInState
import it.polito.lab5.viewModels.LogInViewModel

@Composable
fun SignInScreen(
    vm : LogInViewModel,
    state: SignInState,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    SignInPage(onSignInClick,vm.isSignedIn,vm::setIsSignedInValue)

}