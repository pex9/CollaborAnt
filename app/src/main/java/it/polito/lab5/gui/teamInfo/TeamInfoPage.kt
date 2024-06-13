package it.polito.lab5.gui.teamInfo

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.TextComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInfoTopBar(
    team: Team,
    loggedInUserRole: Role,
    navController: NavController,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    setShowMemberSelBottomSheetValue: (Boolean) -> Unit,
    setShowLeaveDialogValue: (Boolean) -> Unit,
    setShowDeleteDialogValue: (Boolean) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary

    CenterAlignedTopAppBar(
        // Set custom colors for the top app bar
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
        ),
        title = {
            Text(
                text = "Team Info",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = colors.onBackground // Adjust color as needed
            )
        },
        navigationIcon = {
            // Navigation button to navigate back
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background
                    contentColor = colors.onBackground // Dark blue icon color
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding // Standard padding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left), // Back arrow icon
                    contentDescription = "Back Icon", // Description for accessibility
                    tint = colors.onBackground
                )

                Text(
                    text = "Back", // Text displayed next to the back arrow
                    style = MaterialTheme.typography.titleLarge, // Text style
                    fontFamily = interFamily, // Custom font family
                    fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                    fontSize = 20.sp, // Font size
                    color = colors.onBackground
                )
            }
        },
        actions = {
            if(loggedInUserRole == Role.TEAM_MANAGER) {
                // Options component for additional actions
                OptionsComp(
                    team = team,
                    loggedInUserRole = loggedInUserRole,
                    optionsOpened = optionsOpened,
                    setOptionsOpenedValue = setOptionsOpenedValue,
                    navController = navController,
                    setShowMemberSelBottomSheetValue = setShowMemberSelBottomSheetValue,
                    setShowDeleteDialogValue = setShowDeleteDialogValue,
                    setShowLeaveDialogValue = setShowLeaveDialogValue
                )
            } else {
                IconButton(onClick = { setShowLeaveDialogValue(true) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "Leave Icon",
                        tint = colors.onBackground,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun TeamInfoPage(
    scope: CoroutineScope,
    team: Team,
    users: List<User>,
    loggedInUserId: String,
    loggedInUserRole: Role,
    roleSelectionOpened: String,
    setRoleSelectionOpenedValue: (String) -> Unit,
    showMemberOptBottomSheet: Boolean,
    setShowMemberOptBottomSheetValue: (Boolean) -> Unit,
    selectedUser: User?,
    setSelectedUserValue: (User?) -> Unit,
    showLeaveDialog: Boolean,
    setShowLeaveDialogValue: (Boolean) -> Unit,
    showDeleteDialog: Boolean,
    setShowDeleteDialogValue: (Boolean) -> Unit,
    deleteTeam: suspend (Team, List<User>) -> Boolean,
    updateUserRole: suspend (String, Role, Team) -> Unit,
    removeUserFromTeam: suspend (User, Team, String?) -> Unit,
    showMemberSelBottomSheet: Boolean,
    setShowMemberSelBottomSheetValue: (Boolean) -> Unit,
    chosenMember: String?,
    setChosenMemberValue: (String?) -> Unit,
    errorMsg: String,
    setErrorMsgValue: (String) -> Unit,
    setShowDeleteLoadingValue: (Boolean) -> Unit,
    resetUnreadMessage: suspend (Team, String) -> Unit,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            val (first, last) = getMonogramText(team.name)

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(18.dp)
            ) {
                ImagePresentationComp(
                    first = first,
                    last = last,
                    imageProfile = team.image,
                    fontSize = 60.sp
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Text(
                    text = team.name,
                    fontFamily = interFamily, // Custom font family
                    fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                    fontSize = 24.sp, // Font size
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    color = colors.onBackground,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextComp(
                text = team.description.ifBlank { "No description" }, // Default text if description is empty
                label = "Description ", // Label for the description
                minHeight = 125.dp, // Minimum height for the description container
                modifier = Modifier.padding(10.dp), // Padding for the description
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MembersHeaderComp(
                teamId = team.id,
                loggedInUserRole = loggedInUserRole,
                navController = navController
            )

            TeamMembersComp(
                team = team,
                users = users,
                loggedInUserId = loggedInUserId,
                loggedInUserRole = loggedInUserRole,
                updateUserRole = updateUserRole,
                roleSelectionOpened = roleSelectionOpened,
                setRoleSelectionOpenedValue = setRoleSelectionOpenedValue,
                setSelectedUserValue = setSelectedUserValue,
                setShowMemberOptBottomSheetValue = setShowMemberOptBottomSheetValue
            )
        }
    }

    if (showMemberOptBottomSheet && selectedUser != null) {
        MemberOptionsBottomSheet(
            member = selectedUser,
            team = team,
            loggedInUserRole = loggedInUserRole,
            setShowMemberOptBottomSheetValue = setShowMemberOptBottomSheetValue,
            removeUserFromTeam = { removeUserFromTeam(it, team, null) },
            navController = navController,
            loggedInUserId = loggedInUserId,
            setSelectedUserValue = setSelectedUserValue,
            resetUnreadMessage = resetUnreadMessage
        )
    }

    if(showMemberSelBottomSheet) {
        MemberSelectionBottomSheet(
            members = team.members.toList(),
            users = users,
            chosenMember = chosenMember,
            setChosenMemberValue = setChosenMemberValue,
            setErrorMsgValue = setErrorMsgValue,
            setShowLeaveDialogValue = setShowLeaveDialogValue,
            setShowBottomSheetValue = setShowMemberSelBottomSheetValue,
            loggedInUserId = loggedInUserId
        )
    }

    if(showLeaveDialog) {
        DialogComp(
            title = "Confirm Leave",
            text = "Are you sure to leave this team?",
            onConfirmText = "Leave",
            onConfirm = {
                var leaveSuccess = false

                scope.launch {
                    if(team.members.size > 1) {
                        try {
                            users.find { it.id == loggedInUserId }?.let { user ->
                                removeUserFromTeam(user, team, chosenMember)
                                leaveSuccess = true
                            }
                        } catch (e: Exception) {
                            Log.e("DeleteTeam", "Failed to delete team", e)
                            leaveSuccess = false
                        }
                    } else {
                        setShowDeleteLoadingValue(true)
                        leaveSuccess = deleteTeam(team, users)
                    }
                }.invokeOnCompletion {
                    setShowDeleteLoadingValue(false)
                    if(leaveSuccess) {
                        setShowLeaveDialogValue(false)
                        navController.popBackStack(
                            route = "myTeams",
                            inclusive = false
                        )
                    }
                }
            },
            onDismiss = { setShowLeaveDialogValue(false) ; setChosenMemberValue(null) }
        )
    } else if(showDeleteDialog) {
        DialogComp(
            title = "Confirm Delete",
            text = "Are you sure to delete this team?",
            onConfirmText = "Delete",
            onConfirm = {
                var deleteSuccess = false

                scope.launch {
                    setShowDeleteLoadingValue(true)
                    deleteSuccess = deleteTeam(team, users)
                }.invokeOnCompletion {
                    setShowDeleteLoadingValue(false)
                    if(deleteSuccess) {
                        setShowDeleteDialogValue(false)
                        navController.popBackStack(
                            route = "myTeams",
                            inclusive = false
                        )
                    }
                }
            },
            onDismiss = { setShowDeleteDialogValue(false) }
        )
    }

    if(errorMsg.isNotBlank()) {
        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        setErrorMsgValue("")
    }
}

