package it.polito.lab5.gui.myTeams

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch

@Composable
fun TeamItem(team: Team, navController: NavController) {
    val (first, last) = getMonogramText(team.name)

    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
            ) {
                ImagePresentationComp(
                    first = first,
                    last = last,
                    imageProfile = team.image,
                    fontSize = 18.sp
                )
            }
        },
        headlineContent = {
            Text(
                text = team.name,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.White),
        modifier = Modifier.clickable { navController.navigate("myTeams/${team.id}") }
    )
}

@Composable
fun VerifyDomainDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    val instructionsText = "To provide the best user experience with this app, please do the following:\n" +
            "- Click on \"Open Settings\" below.\n" +
            "- Make sure \"Open supported links\" is switched ON.\n" +
            "- Make sure you checked all the links in the dialog after clicking on \"Add link\".\n\n" +
            "Note: You could simply ignore setting the app as the default links handler. " +
            "Though you won't be able to navigate to the app by clicking on external app-related links"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Extra Configurations Needed!",
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    text = instructionsText,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp

                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = "Open Settings",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = "Ignore",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = CollaborantColors.DarkBlue,
        textContentColor = CollaborantColors.BorderGray
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun InvitationTeamBottomSheet(
    team: Team,
    addMember: (String, String) -> Boolean,
    setShowBottomSheetValue: (Boolean) -> Unit, // Callback to toggle the visibility of the bottom sheet
    joinSuccess: Boolean,
    setJoinSuccessValue: (Boolean) -> Unit,
    navController: NavController,
) {
    val (first, last) = getMonogramText(team.name)
    // Remember the state of the modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { setShowBottomSheetValue(false) }, // Dismiss the bottom sheet when requested
        containerColor = CollaborantColors.PageBackGroundGray, // Background color of the bottom sheet
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BottomSheetDefaults.DragHandle()

                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(modifier = Modifier.size(70.dp)) {
                        ImagePresentationComp(
                            first = first,
                            last = last,
                            imageProfile = team.image,
                            fontSize = 28.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                setShowBottomSheetValue(false)
                            }
                        },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.cross), contentDescription = "Close Icon")
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth() // Fill maximum width
    ) {

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = team.name,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if(joinSuccess) { navController.navigate("myTeams/${team.id}") ; setShowBottomSheetValue(false) }
                    else { setJoinSuccessValue(addMember(team.id, DataBase.LOGGED_IN_USER_ID)) }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if(joinSuccess) Color.White else Color.Black,
                    containerColor = if(joinSuccess) CollaborantColors.DarkBlue else CollaborantColors.Yellow
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if(joinSuccess) "View Team" else "Join Team",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


