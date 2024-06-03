package it.polito.lab5

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import it.polito.lab5.googlesignIn.GoogleAuthUiClient
import it.polito.lab5.navigation.AppNavigation
import it.polito.lab5.ui.theme.Lab4Theme
import it.polito.lab5.viewModels.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lab4Theme(darkTheme= false) {
                if (isFirstLaunch(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    appViewModel.setShowDialogValue(checkIfAppApprovedForDomain(this))
                }
                AppNavigation(vm = appViewModel,
                    googleAuthUiClient=googleAuthUiClient,
                    applicationContext=applicationContext,
                    lifecycleOwner=LocalLifecycleOwner.current)

            }
        }

    }
}

fun isFirstLaunch(context: Context): Boolean {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    val isFirstLaunch = sharedPreferences.getBoolean("isFirstLaunch", true)
    if (isFirstLaunch) {
        sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
    }
    return isFirstLaunch
}


@RequiresApi(Build.VERSION_CODES.S)
fun checkIfAppApprovedForDomain(context: Context): Boolean {
    val domain = "https://www.prova.it"
    val manager =  context.getSystemService(DomainVerificationManager::class.java)
    val userState = manager.getDomainVerificationUserState(context.packageName)
    val verifiedDomain =
        userState?.hostToStateMap?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }
    val selectedDomains = userState?.hostToStateMap
        ?.filterValues { it == DomainVerificationUserState.DOMAIN_STATE_SELECTED }

    return (verifiedDomain?.keys?.contains(domain) != true || selectedDomains?.keys?.contains(domain) != true) || !userState.isLinkHandlingAllowed
}