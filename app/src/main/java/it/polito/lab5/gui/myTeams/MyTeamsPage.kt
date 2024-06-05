package it.polito.lab5.gui.myTeams

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyTeamsTopBar() {
    // Get color scheme from MaterialTheme
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.onSecondary,
            titleContentColor = colors.onPrimary,
        ),
        title = {
            Text(
                text = "CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CollaborantColors.DarkBlue,
                            CollaborantColors.Yellow
                        ) // Gradient colors
                    )
                )
            )
        }
    )
}



@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun MyTeamsPage(
    teams: List<Team>,
    invitationTeam: Team?,
    addMember: (String, String) -> Boolean,
    showBottomSheet: Boolean,
    setShowBottomSheetValue: (Boolean) -> Unit,
    showDialog: Boolean,
    setShowDialogValue: (Boolean) -> Unit,
    joinSuccess: Boolean,
    setJoinSuccessValue: (Boolean) -> Unit,
    navController: NavController, // NavController for navigation
    paddingValues: PaddingValues // Padding values for layout
){
    val context = LocalContext.current

    // Composable LazyColumn for displaying list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // Apply padding
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        // Header for personal teams
        item {
            Text(
                text = "My Teams",
                modifier = Modifier.padding(start= 15.dp, top = 20.dp),
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        itemsIndexed(teams) { index, team ->
            TeamItem(team, navController)

            // Add divider except for the last category
            if(index != teams.size - 1) {
                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f)
                )
            }
        }
    }

    if(showDialog) {
        VerifyDomainDialog(
            onDismiss = { setShowDialogValue(false) },
            onConfirm = {
                val intent = Intent(
                    Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
                context.startActivity(intent)
                setShowDialogValue(false)
            }
        )
    }

    if(showBottomSheet && invitationTeam != null) {
        InvitationTeamBottomSheet(
            team = invitationTeam,
            addMember = addMember,
            setShowBottomSheetValue = setShowBottomSheetValue,
            joinSuccess = joinSuccess,
            setJoinSuccessValue = setJoinSuccessValue,
            navController = navController
        )
    }
}

