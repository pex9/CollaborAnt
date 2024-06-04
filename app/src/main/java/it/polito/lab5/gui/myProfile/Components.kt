package it.polito.lab5.gui.myProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.KPI
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
fun TextPresentationComp(
    text: String,
    label: String? = null,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    color: Color = Color.Black,
    icon: @Composable (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(2f)
        ) {
            icon?.let { icon ->
                icon()
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = color
            )
        }

        if(label != null) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = CollaborantColors.BorderGray
                )
            }
        }
    }
}

@Composable
fun OverlappingComponents(
    first: String,
    last: String,
    imageProfile: ImageProfile,
    joinedTeams: Long,
    kpi: Map<String, KPI>
) {

    //val user = Firebase.auth.currentUser
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(292.dp)
        .padding(bottom = 18.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .align(Alignment.BottomCenter)
        ) {
            KPIPresentationComp(kpi, joinedTeams)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .padding(18.dp)
                .align(Alignment.TopCenter)
        )  {
            ImagePresentationComp(
                first = first,
                last = last,
                imageProfile = imageProfile,
                fontSize = 60.sp
            )
        }
    }
}

@Composable
fun KPIPresentationComp(kpi: Map<String, KPI>, joinedTeams: Long) {
    val c = CollaborantColors.BorderGray.copy(0.4f)
    val overallUserKPI = mapOf(
        "assigned" to kpi.values.sumOf{ it.assignedTasks },
        "completed" to kpi.values.sumOf{ it.completedTasks }
    )

    Card(
        border = BorderStroke(1.dp, CollaborantColors.BorderGray),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = joinedTeams.toString(),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                )

                Text(
                    text = "Joined\nTeams",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = CollaborantColors.BorderGray,
                    textAlign = TextAlign.Center
                )
            }

            Divider(modifier = Modifier.width(1.dp).height(40.dp), color = c)

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${overallUserKPI["assigned"]}",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )

                Text(
                    text = "Assigned\nTasks",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = CollaborantColors.BorderGray,
                    textAlign = TextAlign.Center
                )
            }

            Divider(modifier = Modifier.width(1.dp).height(40.dp), color = c)

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${overallUserKPI["completed"]}",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )

                Text(
                    text = "Completed\nTasks",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = CollaborantColors.BorderGray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Composable
fun OptionsComp(
    onSignOut: ()-> Unit,
    optionsOpened: Boolean,
    navController: NavController,
    setShowDialogValue: (Boolean) -> Unit,
    setOptionsOpenedValue: (Boolean) -> Unit,
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
                            text = "Edit Profile",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; navController.navigate("myProfile/edit") },
                    modifier = Modifier.offset(y = (-4).dp) // Offset for better alignment
                )

                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 15.dp).offset(y = (-2).dp)
                )

                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.logout),
                            contentDescription = "Logout Icon",
                            tint = CollaborantColors.DarkBlue
                        )
                    },
                    text = {
                        Text(
                            text = "Log out",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; onSignOut() },
                )

                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 15.dp).offset(y = 2.dp)
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
                            text = "Delete account",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = CollaborantColors.PriorityRed
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; setShowDialogValue(true) },
                    modifier = Modifier.offset(y = 4.dp) // Offset for better alignment
                )
            }
        }
    }
}