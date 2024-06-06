package it.polito.lab5.gui.teamView

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.Tag
import it.polito.lab5.model.Task
import it.polito.lab5.gui.MonogramPresentationComp
import it.polito.lab5.gui.TagCircleComp
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import java.time.format.DateTimeFormatter
import java.util.Locale
import it.polito.lab5.model.User
import it.polito.lab5.viewModels.TeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,  // Task object representing the task item
    users: List<User>,
    colors : ColorScheme,  // ColorScheme for card colors
    navController: NavController  // NavController for navigation
) {
    // Card representing the task item
    Card(
        onClick = { navController.navigate("viewTask/${task.id}")},  // Navigate to task details on click
        modifier = Modifier
            .wrapContentHeight()
            .width(300.dp)
            .padding(bottom = 18.dp),  // Set card width and padding
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,  // Set card background color
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, colors.outline),  // Set card border
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 10.dp, bottom = 8.dp)  // Set padding for task title
            ) {
                // Display task title
                val title = task.title.replaceFirstChar { it.uppercase() }
                Text(
                    title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp, top = 7.dp, bottom = 7.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Display task due date and tag indicator
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
                val color = when (task.tag) { // Determine the color and text to display based on the tag type
                    Tag.LOW -> CollaborantColors.PriorityGreen
                    Tag.MEDIUM -> CollaborantColors.PriorityOrange
                    Tag.HIGH -> CollaborantColors.PriorityOrange2
                    Tag.UNDEFINED -> CollaborantColors.NoPriorityGray
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        task.dueDate?.format(formatter) ?: "",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        letterSpacing = 0.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TagCircleComp(color = color, modifier = Modifier.padding(horizontal = 4.dp).size(6.dp))
                }
            }
        }
        Row { Spacer(modifier = Modifier.height(10.dp)) }  // Add space between title and bottom content
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Display number of comments for the task
                Row(
                    modifier = Modifier.padding(start = 4.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        task.comments.size.toString(),
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 10.sp,
                        letterSpacing = 0.sp
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.chat),
                        contentDescription = "comments",
                        modifier = Modifier
                            .size(15.dp)
                            .padding(start = 4.dp, top = 2.dp, bottom = 2.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 2.dp, top = 10.dp, bottom = 2.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Display team members' profile images
                Row(
                    modifier = Modifier.padding(end = 3.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    task.teamMembers.take(if (task.teamMembers.size > 4) { 3 } else { task.teamMembers.size })
                        .forEach { memberId ->
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(3.dp)
                        ) {
                            users.find { it.id == memberId }?.let { member ->
                                ImagePresentationComp(
                                    first = member.first,
                                    last = member.last,
                                    fontSize = 11.sp,
                                    imageProfile = member.imageProfile
                                )
                            }
                        }
                    }

                    if (task.teamMembers.size > 4) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .padding(3.dp)
                        ) {
                            MonogramPresentationComp(
                                first = "+",
                                last = "${task.teamMembers.size - 3}",
                                fontSize = 11.sp,
                                color = CollaborantColors.Yellow
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(vm: TeamViewModel, hideFilter: () -> Unit) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val colors = MaterialTheme.colorScheme
    // Display the modal bottom sheet for filtering tasks
    ModalBottomSheet(
        onDismissRequest = { hideFilter() },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        sheetState = modalBottomSheetState,
        containerColor = colors.background,
        tonalElevation = 2.dp
    ) {
        // Filter section header
        ListItem(
            headlineContent = {
                Text(
                    text = "Filter",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )

        // Priority filter item
        val literalStatePriority = when (vm.priorityFilter.value) {
            Tag.HIGH -> "High"
            Tag.MEDIUM -> "Medium"
            Tag.LOW -> "Low"
            Tag.UNDEFINED -> "No priority"
            null -> "All"
        }
        ListItem(
            headlineContent = {
                Text(
                    text = "Priority",
                    modifier = Modifier.padding(start = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            trailingContent = {
                Text(
                    text = literalStatePriority,
                    modifier = Modifier.padding(end = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    letterSpacing = 0.sp,
                    color = colors.outline
                )
            },
            modifier = Modifier.clickable {
                vm.nextPriority(vm.priorityFilter.value)
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )

        // My Tasks filter item
        ListItem(
            headlineContent = {
                Text(
                    text = "Only My Tasks",
                    modifier = Modifier.padding(start = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            trailingContent = {
                Switch(
                    checked = vm.myTasksFilter.value,
                    onCheckedChange = { vm.myTasksFilter.value = it },
                    modifier = Modifier.padding(end = 10.dp)
                )
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )

        Spacer(modifier = Modifier.height(5.dp))
        Divider(color = MaterialTheme.colorScheme.outline.copy(0.5f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(5.dp))

        // Sort section header
        ListItem(
            headlineContent = {
                Text(
                    text = "Sort",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )

        // Priority sort item
        ListItem(
            headlineContent = {
                Text(
                    text = "By Priority",
                    modifier = Modifier.padding(start = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            trailingContent = {
                Text(
                    text = vm.prioritySort.value,
                    modifier = Modifier.padding(end = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    letterSpacing = 0.sp,
                    color = colors.outline
                )
            },
            modifier = Modifier.clickable {
                vm.nextSortType(vm.prioritySort.value, vm.prioritySort)
                vm.checkSortCondition(vm.prioritySort)
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )

        // Date sort item
        ListItem(
            headlineContent = {
                Text(
                    text = "By Date",
                    modifier = Modifier.padding(start = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.sp,
                    color = colors.onBackground
                )
            },
            trailingContent = {
                Text(
                    text = vm.dateSort.value,
                    modifier = Modifier.padding(end = 15.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    letterSpacing = 0.sp,
                    color = colors.outline
                )
            },
            modifier = Modifier.clickable {
                vm.nextSortType(vm.dateSort.value, vm.dateSort)
                vm.checkSortCondition(vm.dateSort)
            },
            colors = ListItemDefaults.colors(containerColor = colors.background)
        )
        Spacer(modifier = Modifier.padding(10.dp))
    }
}
