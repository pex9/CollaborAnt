package it.polito.lab5.navigation

import it.polito.lab5.R

data class NavItem(
    val name: String, // Name of the navigation item
    val route: String, // Route to navigate to when the item is selected
    val icon: Int, // Resource ID of the icon associated with the item
    val iconBold: Int
)

val bottomNavItems = listOf(
    NavItem(
        name = "Home", // Name of the navigation item
        route = "myTeams?teamId={teamId}", // Route to navigate to when the item is selected
        icon = R.drawable.home, // Resource ID of the icon associated with the item
        iconBold = R.drawable.home_bold
    ),
    NavItem(
        name = "My Tasks", // Name of the navigation item
        route = "myTasks", // Route to navigate to when the item is selected
        icon = R.drawable.activity, // Resource ID of the icon associated with the item
        iconBold = R.drawable.activity_bold
    ),
    NavItem(
        name = "My Chats", // Name of the navigation item
        route = "myChats", // Route to navigate to when the item is selected
        icon = R.drawable.chat, // Resource ID of the icon associated with the item
        iconBold = R.drawable.chat_bold
    ),
    NavItem(
        name = "My Profile", // Name of the navigation item
        route = "myProfile", // Route to navigate to when the item is selected
        icon = R.drawable.profile, // Resource ID of the icon associated with the item
        iconBold = R.drawable.profile_bold
    )
)