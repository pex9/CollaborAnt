package it.polito.lab5.navigation

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import it.polito.lab5.model.Taken
import it.polito.lab5.model.Uploaded
import it.polito.lab5.screens.IndividualStatsScreen
import it.polito.lab5.screens.MyChatsScreen
import it.polito.lab5.screens.MyProfileFormScreen
import it.polito.lab5.screens.MyProfileScreen
import it.polito.lab5.screens.MyTasksScreen
import it.polito.lab5.screens.MyTeamsScreen
import it.polito.lab5.screens.TaskFormScreen
import it.polito.lab5.screens.TaskHistoryScreen
import it.polito.lab5.screens.TaskViewScreen
import it.polito.lab5.screens.TeamChatScreen
import it.polito.lab5.screens.TeamFormScreen
import it.polito.lab5.screens.TeamInfoViewScreen
import it.polito.lab5.screens.TeamInvitationScreen
import it.polito.lab5.screens.TeamStatsScreen
import it.polito.lab5.screens.TeamViewScreen
import it.polito.lab5.screens.UserProfileScreen
import it.polito.lab5.viewModels.AppFactory
import it.polito.lab5.viewModels.AppViewModel
import it.polito.lab5.viewModels.ChatViewViewModel
import it.polito.lab5.viewModels.IndividualStatsViewModel
import it.polito.lab5.viewModels.MyChatsViewModel
import it.polito.lab5.viewModels.MyProfileFormViewModel
import it.polito.lab5.viewModels.MyProfileViewModel
import it.polito.lab5.viewModels.MyTasksViewModel
import it.polito.lab5.viewModels.MyTeamsViewModel
import it.polito.lab5.viewModels.LogInViewModel
import it.polito.lab5.viewModels.TaskFormViewModel
import it.polito.lab5.viewModels.TaskHistoryViewModel
import it.polito.lab5.viewModels.TaskViewViewModel
import it.polito.lab5.viewModels.TeamFormViewModel
import it.polito.lab5.viewModels.TeamInfoViewModel
import it.polito.lab5.viewModels.TeamInvitationViewModel
import it.polito.lab5.viewModels.TeamStatsViewModel
import it.polito.lab5.viewModels.TeamViewModel
import it.polito.lab5.viewModels.UserProfileViewModel
import it.polito.lab5.screens.LoginScreen
import java.io.File
import java.io.IOException

@Composable
@RequiresApi(Build.VERSION_CODES.S)
fun AppNavigation(vm: AppViewModel, startDestination: String) {
    val context = LocalContext.current
    val navController = rememberNavController() // Remember the navigation controller

    // Set up navigation graph
    NavHost(
        navController = navController,
        startDestination = startDestination   // Starting destination
    ) {

        composable("login") {
            val logInViewModel: LogInViewModel = viewModel()
            LoginScreen(vm = logInViewModel, navController = navController)
        }

        // MyTeams screen
        composable(
            route = "myTeams?teamId={teamId}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "https://team.collaborant.com/{teamId}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(navArgument("teamId") {
                type = NavType.StringType
                nullable = true
            })
        ) {entry ->
            val teamId = entry.arguments?.getString("teamId")
            val myTeamsViewModel: MyTeamsViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    context = context
                )
            )
            MyTeamsScreen(
                vm = myTeamsViewModel,
                showDialog = vm.showDialog,
                setShowDialogValue = vm::setShowDialogValue,
                navController = navController
            )
        }

        // Team add screen
        composable(route = "myTeams/add") {
            val teamFormViewModel: TeamFormViewModel = viewModel(
                factory = AppFactory(
                    teamId = null,
                    context = context
                )
            )
            val galleryContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    teamFormViewModel.setImageValue(Uploaded(uri))
                }
            }

            val cameraContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    // Create a file object from the file path
                    val file = intent?.getStringExtra("filename")?.let { File(it) }

                    // Check if the file exists
                    if (file?.exists() == true) {
                        try {
                            file.inputStream().use { inputStream ->
                                // Decode the input stream into a Bitmap
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                // Set the obtained bitmap in the view model
                                teamFormViewModel.setImageValue(Taken(bitmap))
                            }
                        } catch (e: IOException) {
                            // Handle exception appropriately
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("Error", "File does not exist")
                    }
                }
            }

            TeamFormScreen(vm = teamFormViewModel, cameraContract = cameraContract, galleryContract = galleryContract, navController = navController)
        }

        // Team edit screen
        composable(
            route = "myTeams/edit/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) {entry ->
            val teamId = entry.arguments?.getString("teamId")
            val teamFormViewModel: TeamFormViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    context = context
                )
            )

            val galleryContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    teamFormViewModel.setImageValue(Uploaded(uri))
                }
            }

            val cameraContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    // Create a file object from the file path
                    val file = intent?.getStringExtra("filename")?.let { File(it) }

                    // Check if the file exists
                    if (file?.exists() == true) {
                        try {
                            file.inputStream().use { inputStream ->
                                // Decode the input stream into a Bitmap
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                // Set the obtained bitmap in the view model
                                teamFormViewModel.setImageValue(Taken(bitmap))
                            }
                        } catch (e: IOException) {
                            // Handle exception appropriately
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("Error", "File does not exist")
                    }
                }
            }

            TeamFormScreen(vm = teamFormViewModel, cameraContract = cameraContract, galleryContract =  galleryContract, navController = navController)
        }

        // My tasks screen
        composable(route = "myTasks") {
            val myTaskViewModel: MyTasksViewModel = viewModel(
                factory = AppFactory(context = context)
            )
            MyTasksScreen(
                vm = myTaskViewModel,
                navController = navController
            )
        }

        // Team view screen
        composable(
            route = "myTeams/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { backStackEntry ->
            val teamId = backStackEntry.arguments?.getString("teamId")
            val teamViewModel: TeamViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    context = context
                )
            )

            TeamViewScreen(
                vm = teamViewModel,
                navController = navController
            )
        }

        //  Team Info view screen
        composable(
            route = "infoTeam/{teamId}",
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) { entry ->
            val teamId = entry.arguments?.getString("teamId")
            val teamInfoViewModel: TeamInfoViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    context = context
                )
            )

            TeamInfoViewScreen(vm = teamInfoViewModel, navController = navController)
        }

        composable(
            route = "myTeams/{teamId}/invite",
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) {entry ->
            val teamId = entry.arguments?.getString("teamId")
            val teamInvitationViewModel: TeamInvitationViewModel = viewModel(
                factory = AppFactory(teamId = teamId, context = context)
            )

            TeamInvitationScreen(vm = teamInvitationViewModel, navController = navController)
        }

        // View task screen
        composable(
            route = "viewTask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            val taskId = entry.arguments?.getString("taskId")
            val taskViewViewModel: TaskViewViewModel = viewModel(
                factory = AppFactory(
                    taskId = taskId,
                    context = context
                )
            )
            TaskViewScreen(
                vm = taskViewViewModel,
                navController = navController
            )
        }

        // Add task screen
        composable(
            route = "{teamId}/addTask",
            arguments = listOf(navArgument("teamId") { type = NavType.StringType })
        ) {entry ->
            val teamId = entry.arguments?.getString("teamId")
            val taskFormViewModel: TaskFormViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    taskId = null,
                    context = context
                )
            )

            TaskFormScreen(vm = taskFormViewModel, navController = navController)
        }

        // Edit task screen
        composable(
            route = "editTask/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            val taskId = entry.arguments?.getString("taskId")
            val taskFormViewModel: TaskFormViewModel = viewModel(
                factory = AppFactory(
                    taskId = taskId,
                    context = context
                )
            )

            TaskFormScreen(vm = taskFormViewModel, navController = navController)
        }

        // Task history screen
        composable(
            route = "history/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { entry ->
            val taskId = entry.arguments?.getString("taskId")
            val taskHistoryViewModel: TaskHistoryViewModel = viewModel(
                factory = AppFactory(taskId = taskId, context = context)
            )

            TaskHistoryScreen(vm = taskHistoryViewModel, navController = navController)
        }

        // My chats screen
        composable(route = "myChats") {
            val myChatViewModel: MyChatsViewModel = viewModel(
                factory = AppFactory(context = context)
            )

            MyChatsScreen(
                vm = myChatViewModel,
                navController = navController
            )
        }

        // View team chat
        composable(
            route = "viewChat/{teamId}/{userId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType },
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { entry ->
            val teamId = entry.arguments?.getString("teamId")
            val userId = entry.arguments?.getString("userId")
            val chatViewViewModel: ChatViewViewModel = viewModel(
                factory = AppFactory(
                    teamId = teamId,
                    userId = userId,
                    context = context
                )
            )

            TeamChatScreen (
                vm = chatViewViewModel,
                navController = navController
            )
        }

        // View Individual Stats
        composable(
            route = "viewIndividualStats/{teamId}/{userId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ){ entry ->
            val teamId = entry.arguments?.getString("teamId")
            val userId = entry.arguments?.getString("userId")
            val individualStatsViewModel: IndividualStatsViewModel = viewModel(
                factory = AppFactory(teamId = teamId, userId = userId, context = context)
            )

            if (userId != null) {
                IndividualStatsScreen(vm = individualStatsViewModel, navController = navController)
            }
        }

        // View Team Stats
        composable(
            route = "viewTeamStats/{teamId}",
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType },
            )
        ){ entry ->
            val teamId = entry.arguments?.getString("teamId")
            val teamStatsViewModel: TeamStatsViewModel = viewModel(
                factory = AppFactory(teamId = teamId, context = context)
            )

            TeamStatsScreen(
                vm = teamStatsViewModel,
                navController = navController,
            )
        }

        composable(route = "myProfile")
        {
            val myProfileViewModel: MyProfileViewModel = viewModel(
                factory = AppFactory(context = context)
            )

            MyProfileScreen(
                vm = myProfileViewModel,
                navController = navController
            )
        }

        composable(route = "myProfile/edit"){
            val myProfileFormViewModel: MyProfileFormViewModel = viewModel(
                factory = AppFactory(context = context)
            )
            val galleryContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    myProfileFormViewModel.setImageProfileValue(Uploaded(uri))
                }
            }

            val cameraContract = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    // Create a file object from the file path
                    val file = intent?.getStringExtra("filename")?.let { File(it) }

                    // Check if the file exists
                    if (file?.exists() == true) {
                        try {
                            file.inputStream().use { inputStream ->
                                // Decode the input stream into a Bitmap
                                val bitmap = BitmapFactory.decodeStream(inputStream)

                                // Set the obtained bitmap in the view model
                                myProfileFormViewModel.setImageProfileValue(Taken(bitmap))
                            }
                        } catch (e: IOException) {
                            // Handle exception appropriately
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("Error", "File does not exist")
                    }
                }
            }

            MyProfileFormScreen(
                vm = myProfileFormViewModel,
                navController = navController,
                cameraContract = cameraContract,
                galleryContract = galleryContract,
            )
        }

        composable(
            route = "users/{userId}/profile",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {entry ->
            val userId = entry.arguments?.getString("userId")
            val userProfileViewModel: UserProfileViewModel = viewModel(
                factory = AppFactory(userId = userId, context = context)
            )

            UserProfileScreen(vm = userProfileViewModel, navController = navController)
        }
    }
}
