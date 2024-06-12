package it.polito.lab5.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import it.polito.lab5.gui.myProfile.MyProfilePage
import it.polito.lab5.gui.myProfile.MyProfileTopBar
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyProfileViewModel
import kotlinx.coroutines.launch

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun MyProfileScreen (
    vm: MyProfileViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val loggedInUserId = vm.auth.getSignedInUserId()
    val kpiValues = loggedInUserId?.let { vm.getUserKpi(it) }?.collectAsState(initial = emptyList())?.value
    val user = loggedInUserId?.let { vm.getUser(it) }?.collectAsState(initial = null)?.value?.copy(
        kpiValues = kpiValues?.toMap() ?: emptyMap()
    )

    Scaffold(
        bottomBar = { BottomNavigationBarComp(navController) },
        topBar = {
            MyProfileTopBar(
                onSignOut = {
                    scope.launch {
                        vm.auth.signOut()
                        Toast.makeText(context, "Logout", Toast.LENGTH_LONG).show()
                        navController.navigate("login")
                    }
                },
                optionsOpened = vm.optionsOpened,
                setOptionsOpenedValue = vm::setOptionsOpenedValue,
                navController = navController
            )
        },
   
    ) { paddingValues ->
        user?.let { user ->
            MyProfilePage(
                first = user.first,
                last = user.last,
                location = user.location,
                email = user.email,
                nickname = user.nickname,
                telephone = user.telephone,
                description = user.description,
                imageProfile = user.imageProfile,
                joinedTeams = user.joinedTeams,
                kpi = user.kpiValues,
                paddingValues = paddingValues
            )
        }
    }
}