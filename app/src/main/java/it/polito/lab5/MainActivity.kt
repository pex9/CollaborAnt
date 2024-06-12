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
import it.polito.lab5.model.MyApplication
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import it.polito.lab5.navigation.AppNavigation
import it.polito.lab5.ui.theme.Lab4Theme
import it.polito.lab5.viewModels.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = (this.applicationContext as MyApplication).auth
        val loggedInUserId = auth.getSignedInUserId()
        val startDestination = if(loggedInUserId != null) { "myTeams?teamId={teamId}" }
            else { "login" }

        setContent {
            Lab4Theme(appViewModel = appViewModel) {
                CompositionLocalProvider(LocalTheme provides DarkTheme(appViewModel.themeUserSetting)) {
                    if (isFirstLaunch(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        appViewModel.setShowDialogValue(checkIfAppApprovedForDomain(this))
                    }
                    AppNavigation(vm = appViewModel, startDestination = startDestination)
                }
            }
        }

    }
}

data class DarkTheme(val isDark: Boolean = false)
val LocalTheme = compositionLocalOf { DarkTheme() }


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