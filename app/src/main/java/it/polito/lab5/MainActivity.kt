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
import androidx.compose.runtime.collectAsState
import it.polito.lab5.model.MyApplication
import it.polito.lab5.navigation.AppNavigation
import it.polito.lab5.ui.theme.Lab4Theme
import it.polito.lab5.viewModels.AppViewModel

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = (this.applicationContext as MyApplication).auth
        val model = (this.applicationContext as MyApplication).model
        val loggedInUserId = auth.getSignedInUserId()
        val startDestination = if(loggedInUserId != null) { "myTeams?teamId={teamId}" }
            else { "login" }

        setContent {
            Lab4Theme(darkTheme= false) {
                val teams = loggedInUserId?.let { model.getUserTeams(it).collectAsState(initial = emptyList()).value }
                val chatsReadState = teams?.let { team -> team.map { it.id to (it.unreadMessage[loggedInUserId] ?: false) } }
                chatsReadState?.let { appViewModel.setChatsReadStateValue(it) }

                if (isFirstLaunch(this) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    appViewModel.setShowDialogValue(checkIfAppApprovedForDomain(this))
                }

                AppNavigation(vm = appViewModel, startDestination = startDestination)
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