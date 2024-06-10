package it.polito.lab5.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.teamView.TeamViewPage
import it.polito.lab5.gui.teamView.TeamViewTopBar
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Role
import it.polito.lab5.ui.theme.interFamily
import it.polito.lab5.viewModels.TeamViewModel
import kotlinx.coroutines.delay

@Composable
fun TeamViewScreen(
    vm: TeamViewModel, // ViewModel for team data
    navController: NavController, // NavController for navigation
) {
    val team = vm.teams.collectAsState().value.find { it.id == vm.teamId }
    val loggedInUserRole =  team?.members?.find { it.first == DataBase.LOGGED_IN_USER_ID }?.second
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
            Box(contentAlignment = Alignment.BottomCenter) {
                val containerColor = if(LocalTheme.current.isDark) colors.secondary else colors.primary
                // Floating Action Button
                SmallFloatingActionButton(
                    onClick = { vm.setOptionsOpenedValue(true) }, // Show the dropdown menu on click
                    shape = CircleShape,
                    modifier = Modifier
                        .size(60.dp),
                    containerColor = containerColor // Button color
                ) {
                    // Icon for the floating action button
                    Icon(
                        painter = painterResource(id = R.drawable.category),
                        contentDescription = "More Options",
                        tint = colors.onSecondary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                // Dropdown Menu
                Box(
                    modifier = Modifier
                        .width(68.dp)
                        .background(colors.surfaceColorAtElevation(10.dp))
                        .border(
                            shape = RoundedCornerShape(20.dp),
                            width = 1.dp,
                            color = colors.outline.copy(alpha = 0.5f)
                        ),
                        //.offset(x = 3.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    DropdownMenu(
                        expanded = vm.optionsOpened,
                        onDismissRequest = { vm.setOptionsOpenedValue(false) }, // Dismiss the menu when clicked outside
                        modifier = Modifier.width(68.dp)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,

                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.chart),
                                        contentDescription = "Analytics Icon",
                                        modifier = Modifier.size(25.dp)
                                    )

                                    Text(
                                        text = "Analytics",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            contentPadding = PaddingValues(start = 12.dp),
                            onClick = {
                                vm.setOptionsOpenedValue(false)
                                navController.navigate("viewTeamStats/${vm.teamId}")
                            },
                            colors = MenuDefaults.itemColors(textColor = colors.outline)
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        DropdownMenuItem(
                            text = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.send),
                                        contentDescription = "Team Chat Icon",
                                        modifier = Modifier.size(25.dp)
                                    )

                                    Text(
                                        text = "Chat",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 10.sp
                                    )
                                }
                            },
                            contentPadding = PaddingValues(start = 20.dp),
                            onClick = {
                                vm.setOptionsOpenedValue(false)
                                navController.navigate("viewChat/${vm.teamId}/-1")
                            },
                            colors = MenuDefaults.itemColors(textColor = colors.outline),
                            //modifier = Modifier.padding(vertical = 5.dp)
                        )

                        if(loggedInUserRole == Role.TEAM_MANAGER) {
                            Spacer(modifier = Modifier.height(5.dp))
                            DropdownMenuItem(
                                text = {
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.plus),
                                            contentDescription = "New Task Icon",
                                            modifier = Modifier.size(25.dp)
                                        )

                                        Text(
                                            text = "New Task",
                                            fontFamily = interFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 10.sp
                                        )
                                    }

                                },
                                onClick = {
                                    vm.setOptionsOpenedValue(false)
                                    navController.navigate("${vm.teamId}/addTask")
                                },
                                contentPadding = PaddingValues(start = 10.dp),
                                colors = MenuDefaults.itemColors(textColor = colors.outline),
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        BoxWithConstraints {
            val isHorizontal by remember { mutableStateOf(maxWidth > maxHeight) }
            // Content area
            TeamViewPage(
                vm = vm,
                navController = navController,
                p = paddingValues,
                c = colors,
                filterState = vm.filterState,
                hideFilter = { vm.setFilterStateValue(false) }, // Function to hide filter
                isHorizontal = isHorizontal
            )
        }
    }
}