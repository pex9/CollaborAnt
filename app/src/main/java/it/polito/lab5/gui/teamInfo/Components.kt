package it.polito.lab5.gui.teamInfo

import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.bringPairToHead
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@Composable
fun OptionsComp(
    team: Team,
    loggedInUserRole: Role,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    setShowMemberSelBottomSheetValue: (Boolean) -> Unit,
    setShowLeaveDialogValue: (Boolean) -> Unit,
    setShowDeleteDialogValue: (Boolean) -> Unit,
    navController: NavController
) {
    // Box to align content at the bottom end of the layout
    Box(contentAlignment = Alignment.BottomEnd) {
        // IconButton to trigger the opening/closing of options
        IconButton(onClick = { setOptionsOpenedValue(!optionsOpened) }) {
            Icon(
                painter = painterResource(id = R.drawable.more_circle),
                contentDescription = "Options Icon",
                tint = CollaborantColors.DarkBlue,
                modifier = Modifier.size(32.dp)
            )
        }
        // DropdownMenu to display options
        Box {
            DropdownMenu(
                expanded = optionsOpened,
                onDismissRequest = { setOptionsOpenedValue(false) },
                offset = DpOffset(x = 8.dp, y = 0.dp),
                modifier = Modifier.background(Color.White)
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.edit_square),
                            contentDescription = "Edit Icon",
                            tint = CollaborantColors.DarkBlue
                        )
                    },
                    text = {
                        Text(
                            text = "Edit Team",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; navController.navigate("myTeams/edit/${team.id}") },
                    modifier = Modifier.offset(y = (-4).dp) // Offset for better alignment
                )

                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .offset(2.dp)
                )

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Leave Icon",
                            tint = CollaborantColors.DarkBlue
                        )
                    },
                    text = {
                        Text(
                            text = "Leave Team",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = {
                        setOptionsOpenedValue(false)
                        if(loggedInUserRole == Role.TEAM_MANAGER && team.members.size > 1) { setShowMemberSelBottomSheetValue(true)}
                        else { setShowLeaveDialogValue(true) }

                    },
                )
                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .offset((-2).dp)
                )

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            tint = CollaborantColors.PriorityRed
                        )
                    },
                    text = {
                        Text(
                            text = "Delete Team",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; setShowDeleteDialogValue(true) },
                    modifier = Modifier.offset(y = 4.dp) // Offset for better alignment
                )
            }
        }
    }
}

@Composable
fun MembersHeaderComp(teamId: String, loggedInUserRole: Role, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Members",
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        if(loggedInUserRole == Role.TEAM_MANAGER) {
            Icon(
                painter = painterResource(id = R.drawable.add_user), // Replace with your actual icon resource ID
                contentDescription = "Add member",
                modifier = Modifier
                    .size(26.dp)
                    .clickable { navController.navigate("myTeams/${teamId}/invite") }
            )
        }
    }
}

@Composable
fun TeamMembersComp(
    team: Team,
    users: List<User>,
    loggedInUserId: String,
    loggedInUserRole: Role,
    updateUserRole: suspend (String, Role, Team) -> Unit,
    roleSelectionOpened: String,
    setRoleSelectionOpenedValue: (String) -> Unit,
    setShowMemberOptBottomSheetValue: (Boolean) -> Unit,
    setSelectedUserValue: (User?) -> Unit
) {
    bringPairToHead(team.members.toList(), loggedInUserId).forEach { (memberId, role) ->

        MemberRow(
            users = users,
            memberId = memberId,
            role = role as Role,
            loggedInUserId = loggedInUserId,
            loggedInUserRole = loggedInUserRole,
            updateUserRole = { id, r -> updateUserRole(id, r, team) },
            roleSelectionOpened = roleSelectionOpened,
            setRoleSelectionOpenedValue = setRoleSelectionOpenedValue,
            setSelectedUserValue = setSelectedUserValue,
            setShowBottomSheetValue = setShowMemberOptBottomSheetValue
        )
    }
}

@Composable
fun MemberRow(
    users: List<User>,
    memberId: String,
    role: Role,
    loggedInUserId: String,
    loggedInUserRole: Role,
    updateUserRole: suspend (String, Role) -> Unit,
    roleSelectionOpened: String,
    setRoleSelectionOpenedValue: (String) -> Unit,
    setShowBottomSheetValue: (Boolean) -> Unit,
    setSelectedUserValue: (User?) -> Unit
) {
    val member = users.find { it.id == memberId }
    val optionsOpened = roleSelectionOpened == memberId
    val literalRole = when (role) {
        Role.TEAM_MANAGER -> "Team Manager"
        Role.SENIOR_MEMBER -> "Senior Member"
        Role.JUNIOR_MEMBER -> "Junior Member"
    }

    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
            ) {
                if (member != null) {
                    ImagePresentationComp(
                        first = member.first,
                        last = member.last,
                        imageProfile = member.imageProfile,
                        fontSize = 18.sp
                    )
                }
            }
        },
        headlineContent = {
            Column {
                if (member != null) {
                    Text(
                        text = if(loggedInUserId == member.id) "You" else "${member.first} ${member.last}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        fontFamily = interFamily,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }

                if (loggedInUserRole == Role.TEAM_MANAGER) {
                    RoleOptionsComp(
                        memberId = memberId,
                        role = role,
                        updateUserRole = updateUserRole,
                        roleSelectionOpened = optionsOpened,
                        setRoleSelectionOpenedValue = setRoleSelectionOpenedValue
                    )
                } else {
                    Text(
                        text = literalRole,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        fontFamily = interFamily
                    )
                }
            }
        },
        trailingContent = {
              Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription = "Arrow Icon")
        },
        colors = ListItemDefaults.colors(
            containerColor = CollaborantColors.PageBackGroundGray,
            trailingIconColor = CollaborantColors.DarkBlue
        ),
        modifier = Modifier.clickable {
            setShowBottomSheetValue(true)
            setSelectedUserValue(member)
        }
    )
}

@Composable
fun RoleOptionsComp(
    memberId: String,
    role: Role,
    updateUserRole: suspend (String, Role) -> Unit,
    roleSelectionOpened: Boolean,
    setRoleSelectionOpenedValue: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val literalRole = when (role) {
        Role.TEAM_MANAGER -> "Team Manager"
        Role.SENIOR_MEMBER -> "Senior Member"
        Role.JUNIOR_MEMBER -> "Junior Member"
    }

    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .padding(top = 1.dp, start = 4.dp)
            .clickable(enabled = role != Role.TEAM_MANAGER) {
                setRoleSelectionOpenedValue(memberId)
            }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = literalRole,
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                fontFamily = interFamily
            )

            Spacer(modifier = Modifier.width(6.dp)) // Add space between the text and the icon

            if (roleSelectionOpened) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up),
                    contentDescription = "Arrow Up Icon",
                    modifier = Modifier.size(10.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Arrow down Icon",
                    modifier = Modifier.size(10.dp)

                )
            }
        }

        Box {
            DropdownMenu(
                expanded = roleSelectionOpened,
                onDismissRequest = { setRoleSelectionOpenedValue("") },
                offset = DpOffset(x = 2.dp, y = 2.dp),
                modifier = Modifier.background(Color.White)
            ) {
                Role.entries.drop(1).forEachIndexed { idx, r ->
                    val textRole = when (r) {
                        Role.TEAM_MANAGER -> ""
                        Role.SENIOR_MEMBER -> "Senior Member"
                        Role.JUNIOR_MEMBER -> "Junior Member"
                    }

                    DropdownMenuItem(
                        trailingIcon = {
                            if(r == role) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check Icon",
                                    tint = CollaborantColors.BorderGray,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        },
                        text = {
                            Text(
                                text = textRole,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            scope.launch { updateUserRole(memberId, r) }
                                .invokeOnCompletion {
                                    setRoleSelectionOpenedValue("")
                                }
                        }
                    )

                    if(idx < Role.entries.size - 2) {
                        Divider(
                            color = CollaborantColors.BorderGray.copy(0.4f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MemberOptionsBottomSheet(
    member: User,
    team: Team,
    loggedInUserId: String,
    loggedInUserRole: Role,
    removeUserFromTeam: suspend (User) -> Unit,
    navController: NavController,
    setShowMemberOptBottomSheetValue: (Boolean) -> Unit, // Callback to toggle the visibility of the bottom sheet,
    setSelectedUserValue: (User?) -> Unit
) {
    // Remember the state of the modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    val literalRole = when (team.members[member.id]) {
        Role.TEAM_MANAGER -> "Team Manager"
        Role.SENIOR_MEMBER -> "Senior Member"
        Role.JUNIOR_MEMBER -> "Junior Member"
        null -> ""
    }

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { setShowMemberOptBottomSheetValue(false) }, // Dismiss the bottom sheet when requested
        containerColor = CollaborantColors.PageBackGroundGray, // Background color of the bottom sheet
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BottomSheetDefaults.DragHandle()
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.weight(4f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(60.dp)
                                .padding(4.dp)
                        ) {
                            ImagePresentationComp(
                                first = member.first,
                                last = member.last,
                                imageProfile = member.imageProfile,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = member.first.plus(" ").plus(member.last),
                                fontFamily = interFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )

                            Text(
                                text = literalRole,
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp,
                                fontFamily = interFamily
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { bottomSheetState.hide() }
                                    .invokeOnCompletion {
                                        setShowMemberOptBottomSheetValue(false)
                                        setSelectedUserValue(null)
                                    }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "Close Icon"
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth() // Fill maximum width
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        if(member.id != loggedInUserId) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                ListItem(
                    headlineContent = {},
                    leadingContent = {
                        Text(
                            text = "Info",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.info),
                            contentDescription = "Info Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            setShowMemberOptBottomSheetValue(false)
                            setSelectedUserValue(null)
                            navController.navigate("users/${member.id}/profile")
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = "Analytics",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.chart),
                        contentDescription = "Chart Icon",
                        modifier = Modifier.size(28.dp)
                    )
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        setShowMemberOptBottomSheetValue(false)
                        setSelectedUserValue(null)
                        navController.navigate("viewIndividualStats/${team.id}/${member.id}")
                    }

                },
                colors = ListItemDefaults.colors(containerColor = Color.White)
            )

            if(member.id != loggedInUserId) {
                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ListItem(
                    headlineContent = {},
                    leadingContent = {
                        Text(
                            text = "Chat",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = "Send Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            setShowMemberOptBottomSheetValue(false)
                            setSelectedUserValue(null)
                            navController.navigate("viewChat/${team.id}/${member.id}")
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White)
                )
            }

            if(loggedInUserRole ==  Role.TEAM_MANAGER && member.id != loggedInUserId) {
                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ListItem(
                    headlineContent = {},
                    leadingContent = {
                        Text(
                            text = "Remove from team",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = CollaborantColors.PriorityRed
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            tint = CollaborantColors.PriorityRed,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            coroutineScope.launch {
                                removeUserFromTeam(member)
                            }.invokeOnCompletion {
                                setShowMemberOptBottomSheetValue(false)
                                setSelectedUserValue(null)
                            }
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MemberSelectionBottomSheet(
    members: List<Pair<String, Role>>,
    users: List<User>,
    loggedInUserId: String,
    chosenMember: String?,
    setChosenMemberValue: (String?) -> Unit,
    setErrorMsgValue: (String) -> Unit,
    setShowLeaveDialogValue: (Boolean) -> Unit,
    setShowBottomSheetValue: (Boolean) -> Unit, // Callback to toggle the visibility of the bottom sheet,
) {
    // Remember the state of the modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { // Dismiss the bottom sheet when requested
            setShowBottomSheetValue(false)
            setChosenMemberValue(null)
            setErrorMsgValue("")
        },
        containerColor = CollaborantColors.PageBackGroundGray, // Background color of the bottom sheet
        dragHandle = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BottomSheetDefaults.DragHandle()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                    setShowBottomSheetValue(false)
                                    setChosenMemberValue(null)
                                    setErrorMsgValue("")
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = CollaborantColors.DarkBlue)
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Choose Team Manager",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = {
                                if (chosenMember == null) {
                                    setErrorMsgValue("You must choose the new Team Manager")
                                } else {
                                    coroutineScope.launch { bottomSheetState.hide() }
                                        .invokeOnCompletion {
                                            setShowBottomSheetValue(false)
                                            setShowLeaveDialogValue(true)
                                            setErrorMsgValue("")
                                            setChosenMemberValue(chosenMember)
                                        }
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = CollaborantColors.DarkBlue)
                        ) {
                            Text(
                                text = "Done",
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth() // Fill maximum width
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members.sortedBy { it.second }) { (memberId, role) ->
                val member = users.find { it.id == memberId }
                val literalRole = when (role) {
                    Role.TEAM_MANAGER -> "Team Manager"
                    Role.SENIOR_MEMBER -> "Senior Member"
                    Role.JUNIOR_MEMBER -> "Junior Member"
                }

                if (role != Role.TEAM_MANAGER) {
                    ListItem(
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(4.dp)
                            ) {
                                if (member != null) {
                                    ImagePresentationComp(
                                        first = member.first,
                                        last = member.last,
                                        imageProfile = member.imageProfile,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                        },
                        headlineContent = {
                            Column {
                                if (member != null) {
                                    Text(
                                        text = if (loggedInUserId == member.id) "You" else "${member.first} ${member.last}",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 18.sp,
                                        fontFamily = interFamily,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                }

                                Text(
                                    text = literalRole,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 14.sp,
                                    fontFamily = interFamily
                                )
                            }
                        },
                        trailingContent = {
                            RadioButton(
                                selected = chosenMember == memberId,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CollaborantColors.DarkBlue,
                                    unselectedColor = CollaborantColors.DarkBlue
                                )
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = CollaborantColors.PageBackGroundGray),
                        modifier = Modifier.selectable(
                            selected = chosenMember == memberId,
                            onClick = { setChosenMemberValue(memberId) }
                        )
                    )
                }
            }
        }
    }
}

