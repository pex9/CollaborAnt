package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.myProfile.MyProfilePage
import it.polito.lab5.gui.userProfile.UserProfileTopBar
import it.polito.lab5.viewModels.UserProfileViewModel

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun UserProfileScreen (
    vm: UserProfileViewModel,
    navController: NavController,
) {
    val user = vm.getUser(vm.userId).collectAsState(initial = null).value

    Scaffold(
        topBar = { UserProfileTopBar(navController) },

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