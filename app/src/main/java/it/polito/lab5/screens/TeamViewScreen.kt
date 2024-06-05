package it.polito.lab5.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.teamView.TeamViewPage
import it.polito.lab5.gui.teamView.TeamViewTopBar
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Role
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import it.polito.lab5.viewModels.TeamViewModel
import kotlinx.coroutines.delay

@Composable
fun TeamViewScreen(
    vm: TeamViewModel, // ViewModel for team data
    navController: NavController, // NavController for navigation
) {
    val team = vm.teams.collectAsState().value.find { it.id == vm.teamId }
    val loggedInUserRole =  team?.members?.get(DataBase.LOGGED_IN_USER_ID)
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(key1 = vm.showInfoText) {
        if (vm.showInfoText) {
            delay(3000L)
            vm.setShowInfoTextValue(false)
        }
    }

    // Scaffold for layout structure
    Scaffold(
        topBar = { // Top app bar for task list
            team?.let { team ->
                TeamViewTopBar(
                    team = team,
                    showInfoText = vm.showInfoText,
                    navController = navController,
                    toggleFilterSheet = { vm.setFilterStateValue(!vm.filterState) }
                )
            }
        },
        floatingActionButton = {
            // Box to wrap the Floating Action Button and Dropdown Menu
            Box(contentAlignment = Alignment.BottomEnd) {
                // Floating Action Button
                SmallFloatingActionButton(
                    onClick = { vm.setOptionsOpenedValue(true) }, // Show the dropdown menu on click
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp),
                    containerColor = colors.primary // Button color
                ) {
                    // Icon for the floating action button
                    Icon(
                        painter = painterResource(id = R.drawable.category),
                        contentDescription = "More Options",
                        tint = Color.White
                    )
                }
                // Dropdown Menu
                Box {
                    DropdownMenu(
                        expanded = vm.optionsOpened,
                        onDismissRequest = { vm.setOptionsOpenedValue(false) }, // Dismiss the menu when clicked outside
                        modifier = Modifier.width(77.dp).background(Color.White),
                    ) {
                        DropdownMenuItem(
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.chart),
                                        contentDescription = "Analytics Icon"
                                    )

                                    Text(
                                        text = "Analytics",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            contentPadding = PaddingValues(start = 16.dp),
                            onClick = {
                                vm.setOptionsOpenedValue(false)
                                navController.navigate("viewTeamStats/${vm.teamId}")
                            },
                            colors = MenuDefaults.itemColors(textColor = CollaborantColors.BorderGray)
                        )

                        DropdownMenuItem(
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.send),
                                        contentDescription = "Team Chat Icon"
                                    )

                                    Text(
                                        text = "Team Chat",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            onClick = {
                                vm.setOptionsOpenedValue(false)
                                navController.navigate("viewChat/${vm.teamId}/${null}")
                            },
                            colors = MenuDefaults.itemColors(textColor = CollaborantColors.BorderGray)
                        )

                        if(loggedInUserRole == Role.TEAM_MANAGER) {
                            DropdownMenuItem(
                                text = {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.plus),
                                            contentDescription = "New Task Icon"
                                        )

                                        Text(
                                            text = "New Task",
                                            fontFamily = interFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 10.sp
                                        )
                                    }

                                },
                                contentPadding = PaddingValues(start = 15.dp),
                                onClick = {
                                    vm.setOptionsOpenedValue(false)
                                    navController.navigate("${vm.teamId}/addTask")
                                },
                                colors = MenuDefaults.itemColors(textColor = CollaborantColors.BorderGray)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // Content area
        TeamViewPage(
            vm = vm,
            navController = navController,
            p = paddingValues,
            c = colors,
            filterState = vm.filterState,
            hideFilter = { vm.setFilterStateValue(false) } // Function to hide filter
        )
    }
}