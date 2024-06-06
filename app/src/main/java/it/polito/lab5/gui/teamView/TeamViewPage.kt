package it.polito.lab5.gui.teamView

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.model.TaskState
import it.polito.lab5.viewModels.TeamViewModel
import it.polito.lab5.ui.theme.interFamily
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.DataBase.LOGGED_IN_USER_ID
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.Team

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamViewTopBar(
    team: Team,
    showInfoText: Boolean,
    navController: NavController,
    toggleFilterSheet: () -> Unit
) {
    val (first, last) = getMonogramText(team.name)
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary
    // Center aligned top app bar with title and navigation icon
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            //titleContentColor = colors.onBackground,
        ),
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 5.dp)
                    .clickable { navController.navigate("infoTeam/${team.id}") }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                        .padding(start = 0.dp)
                ) {
                    ImagePresentationComp(
                        first = first,
                        last = last,
                        imageProfile = team.image,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    val infoMsg = "Tap here for more info"
                    val text = if(team.members.size == 1) { "${team.members.size} Member" }
                        else { "${team.members.size} Members" }

                    Text(
                        text = team.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    val infoColor = if(LocalTheme.current.isDark) colors.outline else colors.onBackground
                    Text(
                        text = if(showInfoText) { infoMsg } else { text },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = infoColor
                    )

                }

            }
        },
        navigationIcon = {
            val navIcon = if(LocalTheme.current.isDark) colors.secondary else colors.onBackground
            IconButton(
                onClick = { navController.popBackStack() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = navIcon
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon",
                    tint = colors.onBackground,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        actions = {
            // Filter icon button to toggle filter sheet
            IconButton(onClick = { toggleFilterSheet() }) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.filter),
                    contentDescription = "Localized description",
                    tint = colors.onBackground,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TeamViewPage(
    vm: TeamViewModel, // ViewModel for team data
    navController: NavController, // NavController for navigation
    p: PaddingValues, // Padding values for layout
    c: ColorScheme, // Color scheme for the UI
    filterState: Boolean, // State for showing/hiding filter sheet
    hideFilter: () -> Unit, // Function to hide the filter sheet
    isHorizontal: Boolean = false,
) {
    val tasks = vm.tasks.collectAsState().value.filter { it.teamId == vm.teamId }
    val users = vm.users.collectAsState().value
    // State to remember the scroll position
    val scrollState = rememberLazyListState()
    // Snap behavior for flinging
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                // Adjusting padding to accommodate top and bottom padding
                bottom = p.calculateBottomPadding(),
                top = p.calculateTopPadding(),
            )
            .background(c.background), // Setting background color
        verticalArrangement = Arrangement.spacedBy(16.dp), // Spacing between children
    ) {
        // Automatically scroll to the first visible item on launch
        LaunchedEffect(Unit) { scrollState.animateScrollToItem(scrollState.firstVisibleItemIndex) }
        // LazyRow for horizontal scrolling
        LazyRow(
            modifier = Modifier.fillMaxHeight(),
            state = scrollState,
            flingBehavior = snapBehavior,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Iterate over each task state
            itemsIndexed(TaskState.entries) { index, state ->
                // Convert TaskState enum to literal state string
                val literalState = when (state) {
                    TaskState.NOT_ASSIGNED -> "Not Assigned"
                    TaskState.PENDING -> "Pending"
                    TaskState.IN_PROGRESS -> "In Progress"
                    TaskState.ON_HOLD -> "On Hold"
                    TaskState.COMPLETED -> "Completed"
                    TaskState.OVERDUE -> "Overdue"
                }

                // Filter tasks by state
                var filteredTasks = tasks.filter { it.state == state }

                // Apply priority filter if set
                vm.priorityFilter.value?.let { priority ->
                    filteredTasks = filteredTasks.filter { it.tag == priority }
                }

                // Apply "My Tasks" filter if set
                if (vm.myTasksFilter.value) {
                    filteredTasks =
                        filteredTasks.filter { it.teamMembers.contains(LOGGED_IN_USER_ID) }
                }

                // Sort tasks by priority
                when (vm.prioritySort.value) {
                    "Ascending" -> filteredTasks = filteredTasks.sortedBy { it.tag.ordinal }
                    "Descending" -> filteredTasks =
                        filteredTasks.sortedByDescending { it.tag.ordinal }
                }

                // Sort tasks by due date
                when (vm.dateSort.value) {
                    "Ascending" -> filteredTasks = filteredTasks.sortedBy { it.dueDate }
                    "Descending" -> filteredTasks =
                        filteredTasks.sortedByDescending { it.dueDate }
                }
                val firstItemModifier: Modifier = if(index == 0) Modifier.padding(start = 30.dp) else Modifier
                val lastItemModifier: Modifier = if(state == TaskState.OVERDUE) Modifier.padding(end = 40.dp) else Modifier
                val landscapeModifier = if (isHorizontal) Modifier.width(350.dp) else Modifier.width(330.dp)

                // Column for each task state
                Column(
                    modifier = firstItemModifier
                        .then(lastItemModifier)
                        .then(landscapeModifier)
                        .padding(top = 25.dp)
                ) {
                    val textStateModifier =
                        if (isHorizontal) Modifier.padding(start = 30.dp) else Modifier.padding(
                            start = 20.dp
                        )
                    // Display literal state text
                    Text(
                        text = literalState,
                        modifier = textStateModifier.padding(bottom = 10.dp),
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.sp
                    )

                    // Column for displaying filtered tasks
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Iterate over filtered tasks and display TaskItem for each task
                        filteredTasks.forEach { task ->
                            TaskItem(task, users, c, navController)
                        }
                    }
                }

            }
        }

        // FilterSheet for filtering and sorting tasks
        if(filterState) FilterSheet(vm, hideFilter)
    }
}
