package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import it.polito.lab5.gui.myProfile.MyProfilePage
import it.polito.lab5.gui.myProfile.MyProfileTopBar
import it.polito.lab5.model.DataBase
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyProfileViewModel

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun MyProfileScreen (
    vm: MyProfileViewModel,
    navController: NavController,
    isReadState: MutableList<Pair<Int, Boolean>>,
) {
    val user = vm.users.collectAsState().value.find{
        it.id == DataBase.LOGGED_IN_USER_ID
    }

    Scaffold(
        bottomBar = { BottomNavigationBarComp(navController, isReadState) },
        topBar = { MyProfileTopBar(navController) },
   
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

