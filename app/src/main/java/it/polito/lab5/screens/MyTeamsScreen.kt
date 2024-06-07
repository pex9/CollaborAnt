package it.polito.lab5.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.lab5.gui.myTeams.MyTeamsPage
import it.polito.lab5.gui.myTeams.MyTeamsTopBar
import it.polito.lab5.navigation.BottomNavigationBarComp
import it.polito.lab5.viewModels.MyTeamsViewModel

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun MyTeamsScreen (
    vm: MyTeamsViewModel,
    showDialog: Boolean,
    setShowDialogValue: (Boolean) -> Unit,
    navController: NavController
) {
    val loggedInUserId = vm.auth.getSignedInUserId()
    val loggedInUser = loggedInUserId?.let { vm.getUser(it).collectAsState(initial = null).value }
    val invitationTeam = vm.teamId?.let { vm.getTeam(it).collectAsState(initial = null).value }
    val teams = loggedInUserId?.let { vm.getUserTeams(it).collectAsState(initial = emptyList()).value }

    Scaffold(
        bottomBar = {  BottomNavigationBarComp(navController) },
        topBar = { MyTeamsTopBar() },
        floatingActionButton = {
            // Floating action button for adding a new team
            SmallFloatingActionButton(
                onClick = { navController.navigate("myTeams/add") }, // Navigate to add team screen on click
                shape = CircleShape,
                modifier = Modifier
                    .size(60.dp),
                containerColor = MaterialTheme.colorScheme.primary, // Button color
            ) {
                // Icon for the floating action button
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Icon")
            }
        }
    ) { paddingValues ->
        if (teams != null && loggedInUser != null) {
            MyTeamsPage(
                teams = teams,
                loggedInUser = loggedInUser,
                invitationTeam = invitationTeam,
                addMember = vm::addUserToTeam,
                showBottomSheet = vm.showBottomSheet,
                setShowBottomSheetValue = vm::setShowBottomSheetValue,
                showDialog = showDialog,
                setShowDialogValue = setShowDialogValue,
                joinSuccess = vm.joinSuccess,
                setJoinSuccessValue = vm::setJoinSuccessValue,
                showLoading = vm.showLoading,
                setShowLoadingValue = vm::setShowLoadingValue,
                navController = navController,
                paddingValues = paddingValues
            )
        }
    }
}

