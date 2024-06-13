package it.polito.lab5.gui.myTeams

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MyTeamsTopBar() {
    // Get color scheme from MaterialTheme
    val colors = MaterialTheme.colorScheme

    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary

    val gradientColors =
        if(LocalTheme.current.isDark)
            listOf(
                colors.secondary,
                colors.primary,
            )
        else
            listOf(
                colors.onSurface,
                colors.secondaryContainer,
            )

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
        ),
        title = {
            Text(
                text = "My Teams",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
        },
        actions = { /*No action*/ },
        navigationIcon = {
            Text(
                text = "CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = gradientColors // Gradient colors
                    )
                ),
                modifier = Modifier.padding(start = 5.dp)
            )
        }
    )
}

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun MyTeamsPage(
    teams: List<Team>,
    loggedInUser: User,
    invitationTeam: Team?,
    addMember: suspend (Team, User) -> Boolean,
    showBottomSheet: Boolean,
    setShowBottomSheetValue: (Boolean) -> Unit,
    showDialog: Boolean,
    setShowDialogValue: (Boolean) -> Unit,
    joinSuccess: Boolean,
    setJoinSuccessValue: (Boolean) -> Unit,
    showLoading: Boolean,
    setShowLoadingValue: (Boolean) -> Unit,
    navController: NavController, // NavController for navigation
    paddingValues: PaddingValues // Padding values for layout
){
    val context = LocalContext.current
    val initialText = "Hey ${loggedInUser.first}!\nYou don't have any teams yet. " +
            "You can create one by clicking the '+' button below, or join an existing team using an invitation link."
    val colors = MaterialTheme.colorScheme

    // Composable LazyColumn for displaying list
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        item { Spacer(modifier = Modifier.height(20.dp)) }

        if(teams.isEmpty()){
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 30.dp).padding(top = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surface,
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(width = 1.dp, color = colors.primary),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = initialText,
                        color = colors.outline,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(25.dp),
                        textAlign =  TextAlign.Center,
                        letterSpacing = 0.sp,
                        lineHeight = 30.sp
                    )
                }
            }
        }
        else {
            items(teams) { team ->
                TeamItem(team, navController)
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
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
            loggedInUser = loggedInUser,
            addMember = addMember,
            setShowBottomSheetValue = setShowBottomSheetValue,
            joinSuccess = joinSuccess,
            setJoinSuccessValue = setJoinSuccessValue,
            showLoading = showLoading,
            setShowLoadingValue = setShowLoadingValue,
            navController = navController
        )
    }
}

