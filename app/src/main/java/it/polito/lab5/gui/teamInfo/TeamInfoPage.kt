package it.polito.lab5.gui.teamInfo

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.DialogComp
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.TextComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
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
    CenterAlignedTopAppBar(
        // Set custom colors for the top app bar
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        title = {
            Text(
                text = "Team Info",
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black // Adjust color as needed
            )
        },
        navigationIcon = {
            // Navigation button to navigate back
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background
                    contentColor = CollaborantColors.DarkBlue // Dark blue icon color
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding // Standard padding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left), // Back arrow icon
                    contentDescription = "Back Icon" // Description for accessibility
                )

                Text(
                    text = "Back", // Text displayed next to the back arrow
                    style = MaterialTheme.typography.titleLarge, // Text style
                    fontFamily = interFamily, // Custom font family
                    fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                    fontSize = 20.sp // Font size
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
                        tint = CollaborantColors.DarkBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    )
}

@Composable
fun TeamInfoPage(
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
    removeUserFromTeam: suspend (User, Team) -> Unit,
    showMemberSelBottomSheet: Boolean,
    setShowMemberSelBottomSheetValue: (Boolean) -> Unit,
    chosenMember: String?,
    setChosenMemberValue: (String?) -> Unit,
    errorMsg: String,
    setErrorMsgValue: (String) -> Unit,
    navController: NavController,
    paddingValues: PaddingValues
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
                .padding(horizontal = 28.dp)
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

            Text(
                text = team.name,
                fontFamily = interFamily, // Custom font family
                fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                fontSize = 22.sp, // Font size
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(32.dp))

            TextComp(
                text = team.description.ifBlank { "No description" }, // Default text if description is empty
                label = "Description ", // Label for the description
                minHeight = 125.dp, // Minimum height for the description container
                modifier = Modifier.padding(10.dp) // Padding for the description
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
            removeUserFromTeam = { removeUserFromTeam(it, team) },
            navController = navController,
            loggedInUserId = loggedInUserId,
            setSelectedUserValue = setSelectedUserValue,
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
                setShowLeaveDialogValue(false)

                scope.launch {
                    if (chosenMember != null) {
                        updateUserRole(chosenMember, Role.TEAM_MANAGER, team)
                    }
                }.invokeOnCompletion {
                    if(team.members.size > 1) {
                        users.find { it.id == loggedInUserId }?.let { user ->
                            scope.launch {
                                removeUserFromTeam(user, team)
                            }.invokeOnCompletion {
                                navController.popBackStack(
                                    route = "myTeams",
                                    inclusive = false
                                )
                            }
                        }
                    }
//                    else {
//                        scope.launch {
//                            deleteTeam(team, users) //  TODO: fix this
//                        }.invokeOnCompletion {
//                            navController.popBackStack(
//                                route = "myTeams",
//                                inclusive = false
//                            )
//                        }
//                    }
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
                setShowDeleteDialogValue(false)
                scope.launch {
                    Log.e("Server Error", "Ciao")
                    if (deleteTeam(team, users)) {
                        Log.e("Server Error", "Eccomi")
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

