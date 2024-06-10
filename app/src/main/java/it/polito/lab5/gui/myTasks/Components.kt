package it.polito.lab5.gui.myTasks

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Task
import it.polito.lab5.gui.TagCircleComp
import it.polito.lab5.gui.TaskStateComp
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksTopBar() {
    // Get color scheme from MaterialTheme
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.onSecondary,
            titleContentColor = colors.onPrimary,
        ),
        title = {
            Text(
                text = "CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CollaborantColors.DarkBlue,
                            CollaborantColors.Yellow
                        ) // Gradient colors
                    )
                )
            )
        }
    )
}

// Composable for displaying tasks belonging to a category
@Composable
fun CategoryItem(
    teams: List<Team>,
    tasks: List<Task>,
    category: String,
    loggedInUserId: String,
    navController: NavController,
    setIsDialogOpen: (Boolean) -> Unit,
    setCurrentCategory: (String) -> Unit,
    setCategorySelectionOpenedValue: (String) -> Unit,
    categorySelectionOpened: String,
    setCategory: (String) -> Unit,
    setMyTasksHideSheet: (Boolean) -> Unit,
    setTargetTaskIdValue: (String) -> Unit,
    expandCategory: String,
    setExpandCategory: (String) -> Unit,
    setIsDialogDeleteOpen: (Boolean) -> Unit,
    setNumberOfTasksForCategory: (Int?) -> Unit,
    setChosenCategoryValue: (String) -> Unit,
) {
    // Filter tasks for the current category
    val userTasks = tasks.filter { it.categories[loggedInUserId] == category }
    val flag = expandCategory == category

    // Row containing category name and task count
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { setExpandCategory(if(flag) "" else category) }, // Toggle expansion on click
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // First column containing arrow and category name
        Column(
            modifier = Modifier
                .weight(1.5f)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                val arrowIcon = if (flag) R.drawable.arrow_down else R.drawable.arrow_right
                // Arrow icon for expanding/collapsing category
                Icon(
                    imageVector = ImageVector.vectorResource(id = arrowIcon),
                    contentDescription = "Localized description",
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Display category name
                Text(
                    text = category,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    letterSpacing = 0.sp
                )
            }
        }
        // Second column containing task count
        Row(
            modifier = Modifier
                .weight(0.5f)
                .padding(5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display number of tasks for the category
            Text(
                text = userTasks.size.toString(),
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp,
                modifier = Modifier.padding(end = 10.dp)
            )

            if(category != "Recently Assigned") {
                CategoryOptionsComp(
                    setCategorySelectionOpenedValue = setCategorySelectionOpenedValue,
                    setIsDialogOpen = setIsDialogOpen,
                    setCurrentCategory = setCurrentCategory,
                    category = category,
                    categorySelectionOpened = categorySelectionOpened,
                    setCategory = setCategory,
                    setIsDialogDeleteOpen = setIsDialogDeleteOpen,
                    setNumberOfTasksForCategory = setNumberOfTasksForCategory,
                    numberOfTasks= userTasks.size
                )
            } else {
                val context = LocalContext.current
                Box(contentAlignment = Alignment.BottomEnd) {
                    IconButton(onClick = { Toast.makeText(context, "This category can't be edited", Toast.LENGTH_SHORT).show() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.lock),
                            contentDescription = "Blocked category",
                            tint = CollaborantColors.DarkBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
    // If category is expanded, display tasks
    if (flag) {
        userTasks.forEach { task ->
            val taskTeam = teams.find { it.id == task.teamId }

            Divider(color = CollaborantColors.BorderGray.copy(0.4f), thickness = 1.dp)

            if (taskTeam != null) {
                TaskItem(
                    team = taskTeam,
                    task = task,
                    navController = navController,
                    setMyTasksHideSheet = setMyTasksHideSheet,
                    setTargetTaskIdValue = setTargetTaskIdValue,
                    setChosenCategory = { setChosenCategoryValue(category) }
                )
            }
        }
    }
}

// Composable for displaying individual task item
@Composable
fun TaskItem(
    team: Team,
    task: Task,
    navController: NavController,
    setMyTasksHideSheet: (Boolean) -> Unit,
    setTargetTaskIdValue: (String) -> Unit,
    setChosenCategory: () -> Unit
) {
    // Column for displaying task details
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CollaborantColors.PageBackGroundGray)
            .padding(5.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { navController.navigate("viewTask/${task.id}") },
                    onLongPress = { setMyTasksHideSheet(true); setTargetTaskIdValue(task.id); setChosenCategory() },
                )
            }
    ) {
        // Row for task title
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                // Display task title
                val title = task.title.replaceFirstChar { it.uppercase() }
                Text(
                    title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.sp
                )
            }
            // Display task status and tag
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    // Determine tag color based on task priority
                    val tagContainerColor: Color = when (task.tag) {
                        Tag.HIGH -> CollaborantColors.PriorityOrange2
                        Tag.MEDIUM -> CollaborantColors.PriorityOrange
                        Tag.LOW -> CollaborantColors.PriorityGreen
                        else -> CollaborantColors.NoPriorityGray
                    }
                    // Display task state and tag circle
                    TaskStateComp(state = task.state, fontSize = 13.sp)
                    TagCircleComp(color = tagContainerColor, modifier = Modifier
                        .padding(start = 12.dp)
                        .size(6.dp))
                }
            }
        }
        // Spacer between rows
        Row(modifier = Modifier.fillMaxWidth()) { Spacer(modifier = Modifier.height(7.dp)) }
        // Row for task category and due date
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 13.dp, top = 10.dp, bottom = 2.dp)
            ) {
                // Display task category
                Text(
                    text = team.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp
                )
            }
            // Display task due date
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 7.dp, top = 10.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
                Text(
                    task.dueDate?.format(formatter)?: "",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}

// Alert dialog for confirming deletion
@Composable
fun CategoryOptionsComp(
    categorySelectionOpened: String,
    setIsDialogOpen: (Boolean) -> Unit,
    setCurrentCategory: (String) -> Unit,
    category: String,
    setCategorySelectionOpenedValue: (String) -> Unit,
    setCategory: (String) -> Unit,
    setIsDialogDeleteOpen: (Boolean) -> Unit,
    setNumberOfTasksForCategory: (Int?) -> Unit,
    numberOfTasks: Int
) {
    // Box to align content at the bottom end of the layout
    Box(contentAlignment = Alignment.BottomEnd) {
        val flag = categorySelectionOpened == category
        // IconButton to trigger the opening/closing of options
        IconButton(onClick = { setCategorySelectionOpenedValue(category) ; }) {
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
                expanded = flag,
                onDismissRequest = { setCategorySelectionOpenedValue("") },
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
                            text = "Edit Category",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = {
                        setCategorySelectionOpenedValue("")
                        setCurrentCategory(category)
                        setCategory(category)
                        setIsDialogOpen(true)
                    },
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
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            tint = CollaborantColors.PriorityRed
                        )
                    },
                    text = {
                        Text(
                            text = "Delete Category",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = CollaborantColors.PriorityRed
                        )
                    },
                    onClick = {
                        setCategorySelectionOpenedValue("")
                        setIsDialogDeleteOpen(true);setCurrentCategory(category)
                        setNumberOfTasksForCategory(numberOfTasks)
                    },
                )
            }
        }
    }
}

//
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTasksModalBottomSheet(
    setMyTasksHideSheet: (Boolean) -> Unit,
    categories: List<String>,
    updateCategoryFromTask: (String, String, String) -> Unit,
    taskId: String?,
    chosenCategory: String,
    setChosenCategoryValue: (String) -> Unit,
){
    val modalBottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()


    // Display the modal bottom sheet for filtering tasks
    ModalBottomSheet(
        onDismissRequest = { setMyTasksHideSheet(false) },
        modifier = Modifier
            .fillMaxWidth(),
        sheetState = modalBottomSheetState,
        containerColor = CollaborantColors.PageBackGroundGray,
        dragHandle = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BottomSheetDefaults.DragHandle()

                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Choose Category",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center))

                    IconButton(
                        onClick = {
                            coroutineScope.launch { modalBottomSheetState.hide() }.invokeOnCompletion {
                                setMyTasksHideSheet(false)
                            }
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.cross), contentDescription = "Close Icon")
                    }
                }
            }
        },
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = category,
                            fontWeight = FontWeight.Medium,
                            fontSize = 18.sp,
                            fontFamily = interFamily,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    },
                    trailingContent = {
                        RadioButton(
                            selected = chosenCategory == category,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = CollaborantColors.DarkBlue,
                                unselectedColor = CollaborantColors.DarkBlue
                            )
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = CollaborantColors.PageBackGroundGray),
                    modifier = Modifier.selectable(
                        selected = chosenCategory == category,
                        onClick = {
                            coroutineScope.launch { modalBottomSheetState.hide() }.invokeOnCompletion {
                                if(taskId != null) {
                                    updateCategoryFromTask(taskId, DataBase.LOGGED_IN_USER_ID, category)
                                }

                                setChosenCategoryValue("")
                                setMyTasksHideSheet(false)
                            }
                        }
                    )
                )
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
