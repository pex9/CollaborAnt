package it.polito.lab5.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.polito.lab5.model.MyApplication
import it.polito.lab5.ui.theme.interFamily

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBarComp(navController: NavController) {
    // Get the current back stack entry
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val auth = (context.applicationContext as MyApplication).auth
    val model = (context.applicationContext as MyApplication).model
    val loggedInUserId = auth.getSignedInUserId()
    val teams = loggedInUserId?.let {
        model.getUserTeams(it).collectAsState(initial = emptyList()).value
    }
    val isReadState = teams?.let { team ->
        team.map {
            it.id to (it.unreadMessage[loggedInUserId] ?: false)
        }
    }
    val myChatsNotificationsCount = isReadState?.count{ it.second } ?: 0

    val colors = MaterialTheme.colorScheme
    val containerColor = colors.surfaceColorAtElevation(10.dp)

    // Bottom navigation bar
    NavigationBar(
        containerColor = containerColor // Set container color
    ) {
        // Iterate over bottom navigation items
        bottomNavItems.forEach { navItem ->
            NavigationBarItem(
                // Check if the current destination matches the navigation item route
                //selected = currentDestination?.hierarchy?.any { it.route == navItem.route } == true,
                selected = currentRoute == navItem.route,
                onClick = {
                    // Navigate to the selected destination
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true // Save navigation state
                        }

                        launchSingleTop = true // Launch as single top
                        restoreState = true // Restore navigation state
                    }
                },
                icon = {
                    // Icon for navigation item
                    if (navItem.route != "myChats"){
                        Icon(
                            painter = painterResource(
                                // Use bold icon if the current destination matches the navigation item route
                                if (currentRoute == navItem.route) navItem.iconBold else navItem.icon
                            ),
                            contentDescription = "bottom bar item icon",
                            tint = if (currentRoute == navItem.route) colors.secondaryContainer else colors.outline, // Set icon tint based on selection
                            modifier = Modifier.size(28.dp) // Set icon size
                        )
                    } else {
                        BadgedBox(
                            badge = {
                                if (myChatsNotificationsCount > 0) {
                                    Badge(
                                        containerColor = colors.error,
                                        contentColor = colors.onError
                                    ) {
                                        Text(myChatsNotificationsCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                painter = painterResource(
                                    // Use bold icon if the current destination matches the navigation item route
                                    if (currentRoute == navItem.route) navItem.iconBold else navItem.icon
                                ),
                                contentDescription = "bottom bar item icon",
                                tint = if (currentRoute == navItem.route) colors.secondaryContainer else colors.outline, // Set icon tint based on selection
                                modifier = Modifier.size(28.dp) // Set icon size
                            )
                        }
                    }
                },
                label = {
                    val weight = if (currentRoute == navItem.route) FontWeight.Bold else FontWeight.Normal
                    // Label for navigation item
                    Text(
                        text = navItem.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = weight,
                        fontSize = 10.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(top = 2.dp), // Add padding

                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = containerColor,
                    indicatorColor = containerColor,
                    unselectedTextColor = colors.onBackground, // Set text color for unselected state
                    selectedTextColor = colors.secondaryContainer // Set text color for selected state
                )
            )
        }
    }
}