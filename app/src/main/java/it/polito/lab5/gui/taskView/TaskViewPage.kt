package it.polito.lab5.gui.taskView

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.TextComp
import it.polito.lab5.model.Attachment
import it.polito.lab5.model.Role
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskTopBar(
    taskId: String,
    isDelegatedMember: Boolean,
    loggedInUserRole: Role,
    state: TaskState,
    updateState: (TaskState) -> Unit,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    stateSelOpened: Boolean,
    setStateSelOpenedValue: (Boolean) -> Unit,
    setShowDeleteDialogValue: (Boolean) -> Unit,
    navController: NavController,
) {
    // Top App Bar for the Task screen
    CenterAlignedTopAppBar(
        // Set custom colors for the top app bar
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        title = {}, // No title for this top app bar
        navigationIcon = {
            // Navigation button to navigate back
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background
                    contentColor = CollaborantColors.DarkBlue // Dark blue icon color
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding // Standard padding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left), // Back arrow icon
                    contentDescription = "Back Icon" // Description for accessibility
                )

                Text(
                    text = "Back", // Text displayed next to the back arrow
                    style = MaterialTheme.typography.titleLarge, // Text style
                    fontFamily = interFamily, // Custom font family
                    fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                    fontSize = 20.sp // Font size
                )
            }
        },
        actions = {
            // Task state selection component
            TaskStateSelComp(
                isDelegatedMember = isDelegatedMember,
                loggedInUserRole = loggedInUserRole,
                state = state,
                updateState = updateState,
                stateSelOpened = stateSelOpened,
                setStateSelOpenedValue = setStateSelOpenedValue
            )
            
            Spacer(modifier = Modifier.width(3.dp))

            if(loggedInUserRole == Role.TEAM_MANAGER) {
                // Options component for additional actions
                OptionsComp(
                    taskId = taskId,
                    optionsOpened = optionsOpened,
                    setOptionsOpenedValue = setOptionsOpenedValue,
                    setShowDeleteDialogValue = setShowDeleteDialogValue,
                    navController = navController
                )
            } else {
                IconButton(onClick = { navController.navigate("history/${taskId}") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.time_circle),
                        contentDescription = "History Icon",
                        tint = CollaborantColors.DarkBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

        }
    )
}

@Composable
fun TaskPage(
    task: Task,
    users: List<User>,
    isDelegatedMember: Boolean,
    loggedInUserRole: Role,
    addAttachment: (String, Attachment) -> Unit,
    removeAttachment: (String, String) -> Unit,
    setShowBottomSheetValue: (Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 28.dp, end = 28.dp)
            .verticalScroll(rememberScrollState()) // Enable vertical scrolling
    ) {
        Spacer(modifier = Modifier.height(12.dp)) // Spacer for layout spacing

        // Display task title
        TextComp(
            text = task.title,
            label = "Task title:", // Label for the title
            minHeight = 50.dp, // Minimum height for the title container
            modifier = Modifier.padding(14.dp) // Padding for the title
        )

        // Card to contain task details
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp), // Padding for the card
            colors = CardDefaults.cardColors(
                containerColor = Color.White, // White background for the card
                contentColor = Color.Black // Black text color for content
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Card elevation
            shape = CardDefaults.elevatedShape, // Rounded corner shape for the card
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Height for the row
                ) {
                    TagComp(tag = task.tag) // Display task tag
                }

                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp) // Height for the row
                ) {
                    DueDateComp(date = task.dueDate) // Display due date
                }

                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Delegated member component
                DelegatedMemberComp(
                    members = task.teamMembers,
                    users = users,
                    setShowBottomSheetValue = setShowBottomSheetValue
                )

                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Repeat component
                RepeatComponent(repeat = task.repeat)
            }
        }

        // Display task description
        TextComp(
            text = task.description.ifBlank { "No description" }, // Default text if description is empty
            label = "Description:", // Label for the description
            minHeight = 125.dp, // Minimum height for the description container
            modifier = Modifier.padding(10.dp) // Padding for the description
        )

        // Attachment component
        AttachmentComponent(
            taskId = task.id,
            isDelegatedMember = isDelegatedMember,
            loggedInUserRole = loggedInUserRole,
            attachments = task.attachments,
            addAttachment = addAttachment,
            removeAttachment = removeAttachment
        )

        // Comments component
        CommentsComp(comments = task.comments, users = users)

        Spacer(modifier = Modifier.height(88.dp)) // Spacer for layout spacing
    }
}
