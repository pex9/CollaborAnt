package it.polito.lab5.gui.taskView

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.Attachment
import it.polito.lab5.model.Comment
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.User
import it.polito.lab5.gui.MonogramPresentationComp
import it.polito.lab5.gui.TagCircleComp
import it.polito.lab5.gui.TaskStateComp
import it.polito.lab5.gui.bringPairToHead
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun OptionsComp(
    taskId: String,
    repeat: Repeat,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    setShowRepeatDeleteDialogValue: (Boolean) -> Unit,
    setShowDeleteDialogValue: (Boolean) -> Unit,
    navController: NavController
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

        // DropdownMenu to display task options
        Box {
            DropdownMenu(
                expanded = optionsOpened,
                onDismissRequest = { setOptionsOpenedValue(false) },
                offset = DpOffset(x = 8.dp, y = 0.dp),
                modifier = Modifier.background(Color.White)
            ) {
                // Task History option
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.time_circle),
                            contentDescription = "History Icon",
                            tint = CollaborantColors.DarkBlue
                        )
                    },
                    text = {
                        Text(
                            text = "Task History",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; navController.navigate("history/${taskId}") },
                    modifier = Modifier.offset(y = (-4).dp) // Offset for better alignment
                )

                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .offset(y = (-2).dp)
                )

                // Edit Task option
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
                            text = "Edit Task",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    onClick = { setOptionsOpenedValue(false) ; navController.navigate("editTask/${taskId}") },
                )

                Divider(
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .offset(y = 2.dp)
                )

                // Task Delete option
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
                            text = "Delete Task",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                    },
                    onClick = {
                        setOptionsOpenedValue(false)
                        if(repeat == Repeat.NEVER) { setShowDeleteDialogValue(true) }
                        else { setShowRepeatDeleteDialogValue(true) }
                    },
                    modifier = Modifier.offset(y = 4.dp) // Offset for better alignment
                )
            }
        }
    }
}

@Composable
fun TaskStateSelComp(
    state: TaskState,
    updateState: (TaskState) -> Unit,
    stateSelOpened: Boolean,
    setStateSelOpenedValue: (Boolean) -> Unit,
    isDelegatedMember: Boolean,
    loggedInUserRole: Role
) {
    val check = state != TaskState.COMPLETED && state != TaskState.NOT_ASSIGNED && state != TaskState.OVERDUE
            && (loggedInUserRole == Role.TEAM_MANAGER || isDelegatedMember)

    // Box to align content at the bottom end of the layout
    Box(contentAlignment = Alignment.CenterEnd) {
        // Display the current task state and toggle its selection
        TaskStateComp(
            state = state,
            fontSize = 18.sp,
            onClick = if(check) { { setStateSelOpenedValue(!stateSelOpened) } } else { null },
            trailingIcon = if(check) {
                    {
                        if (stateSelOpened) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_up),
                                contentDescription = "Arrow Up Icon",
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_down),
                                contentDescription = "Arrow down Icon",
                                modifier = Modifier.padding(end = 10.dp)
                            )
                        }
                    }
                } else { null }
        )

        // DropdownMenu to select task state
        Box {
            DropdownMenu(
                expanded = stateSelOpened,
                onDismissRequest = { setStateSelOpenedValue(false) },
                modifier = Modifier
                    .background(Color.White)
                    .width(150.dp),
                offset = DpOffset(x = 8.dp, y = 0.dp)
            ) {
                val excludedStates = setOf(TaskState.NOT_ASSIGNED, TaskState.PENDING, TaskState.OVERDUE)
                val states = TaskState.entries.filter { it !in excludedStates }

                states.forEachIndexed { idx, taskState ->
                    val literalState = when (taskState) {
                        TaskState.NOT_ASSIGNED -> ""
                        TaskState.PENDING -> ""
                        TaskState.IN_PROGRESS -> "In progress"
                        TaskState.ON_HOLD -> "On-hold"
                        TaskState.COMPLETED -> "Completed"
                        TaskState.OVERDUE -> ""
                    }

                    val offset = when (idx) {
                        0 -> { (-2).dp }
                        states.size - 1 -> { 2.dp }
                        else -> { 0.dp }
                    }

                    // DropdownMenuItem for each task state
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = literalState,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        },
                            trailingIcon = if(state == taskState) {
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check Icon",
                                    tint = CollaborantColors.BorderGray,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        } else { null },
                        onClick = { setStateSelOpenedValue(false); updateState(taskState) },
                        modifier = Modifier.offset(y = offset) // Offset for better alignment
                    )

                    if (idx < states.size - 1) {
                        Divider(
                            thickness = 1.dp,
                            color = CollaborantColors.BorderGray.copy(0.4f),
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagComp(tag: Tag, updateExpanded: (() -> Unit)? = null, trailingIcon: @Composable (() -> Unit)? = null) {
    val isEdit = trailingIcon != null // Check if there's a trailing icon (indicating editing mode)
    val (color, text) = when (tag) { // Determine the color and text to display based on the tag type
        Tag.LOW -> CollaborantColors.PriorityGreen to "Low"
        Tag.MEDIUM -> CollaborantColors.PriorityOrange to "Medium"
        Tag.HIGH -> CollaborantColors.PriorityOrange2 to "High"
        Tag.UNDEFINED -> CollaborantColors.NoPriorityGray to if (isEdit) "Add Priority" else "No Priority"
    }

    // Row to organize the tag icon, text, and trailing icon (if present) horizontally
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = isEdit, onClick = { updateExpanded?.let { updateExpanded() } }) // Enable click behavior in editing mode
    ) {
        // Column to hold the tag icon
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            // Tag icon displayed with a tint of dark blue color
            Icon(
                painter = painterResource(id = R.drawable.tag),
                contentDescription = "Tag Icon",
                tint = CollaborantColors.DarkBlue
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Priority:",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
            )
        }

        // Row to hold the tag text and trailing icon (if present)
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Text displaying the tag content with normal font weight
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )

            // Display trailing icon (e.g., edit icon or custom icon)
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing between text and trailing icon
                trailingIcon() // Render the trailing icon
                Spacer(modifier = Modifier.width(16.dp)) // Add spacing after trailing icon
            } else {
                TagCircleComp(color = color, modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(8.dp)) // Display default tag circle with specified color
            }
        }
    }
}

@Composable
fun DueDateComp(
    date: LocalDate?, // The due date
    isEdit: Boolean = false, // Flag indicating if the component is in edit mode
    updateVisible: (() -> Unit)? = null // Function to update visibility (optional)
) {
    // Row containing the due date component
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = isEdit, onClick = { updateVisible?.let { updateVisible() } }) // Enable click handling if in edit mode
    ) {
        // Column for the calendar icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar), // Calendar icon
                contentDescription = "Calendar Icon",
                tint = CollaborantColors.DarkBlue
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Due date:",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
            )
        }

        // Column for displaying the date text
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            // Format the date text
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            val text = if (date != null) { date.format(formatter) }
            else if (isEdit) { "Add due date" } // Display "Add due date" if in edit mode and no date is set
            else { "No date" } // Display "No date" if no date is set

            // Display the formatted date text
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}
@Composable
fun EndRepeatDateComp(
    date: LocalDate?, // The due date
    isEdit: Boolean = false, // Flag indicating if the component is in edit mode
    updateVisible: (() -> Unit)? = null // Function to update visibility (optional)
) {
    // Row containing the due date component
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = isEdit, onClick = { updateVisible?.let { updateVisible() } }) // Enable click handling if in edit mode
    ) {
        // Column for the calendar icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.calendar), // Calendar icon
                contentDescription = "Calendar Icon",
                tint = CollaborantColors.DarkBlue
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "End date:",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
            )
        }

        // Column for displaying the date text
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            // Format the date text
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            val text = if (date != null) { date.format(formatter) }
            else if (isEdit) { "Add end date" } // Display "Add due date" if in edit mode and no date is set
            else { "No date" } // Display "No date" if no date is set

            // Display the formatted date text
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DelegatedMemberComp(
    members: List<String>, // List of member IDs
    users: List<User>,
    setShowBottomSheetValue: (Boolean) -> Unit, // Function to set the visibility of the bottom sheet
    isEdit: Boolean = false // Flag indicating if the component is in edit mode
) {
    // Row containing the delegated member component
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = { setShowBottomSheetValue(true) }) // Click listener to show the bottom sheet
    ) {
        // Row for the users icon and delegated to label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
                .padding(start = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.users), // Users icon
                contentDescription = "Users Icon",
                tint = CollaborantColors.DarkBlue,
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Display "Delegated to:" label if not in edit mode or if members are present, else display "Select delegated members"
            val delegatedToText = if (!isEdit || members.isNotEmpty()) { "Delegated to:" }
            else { "Select delegated members" }
            Text(
                text = delegatedToText,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
            )
        }

        // Row for displaying member avatars
        if (members.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                // Display avatars for up to 2 members
                members.take(if (members.size > 3) { 2 } else { members.size })
                    .forEach { memberId ->
                        // Find the member by ID and display their avatar
                        users.find { it.id == memberId }?.let { member ->
                            Box(modifier = Modifier
                                .size(32.dp)
                                .padding(2.5.dp)) {
                                // Display monogram presentation if the member's image profile is empty
                                ImagePresentationComp(
                                    first = member.first,
                                    last = member.last,
                                    imageProfile = member.imageProfile,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                // If more than 3 members, display a "+" avatar indicating additional members
                if (members.size > 3) {
                    Box(modifier = Modifier
                        .size(32.dp)
                        .padding(2.5.dp)) {
                        MonogramPresentationComp(
                            first = "+",
                            last = (members.size - 2).toString(),
                            fontSize = 13.sp,
                            color = CollaborantColors.Yellow
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun RepeatComponent(
    repeat: Repeat, // The repeat type (e.g., NEVER, DAILY, WEEKLY, MONTHLY)
    updateExpanded: (() -> Unit)? = null, // Function to update the expanded state (optional)
    trailingIcon: @Composable (() -> Unit)? = null // Trailing icon composable (optional)
) {
    // Check if the component is in edit mode
    val isEdit = trailingIcon != null
    // Convert the repeat enum to a literal string representation
    val literalRepeat = when (repeat) {
        Repeat.NEVER -> "Never"
        Repeat.DAILY -> "Daily"
        Repeat.WEEKLY -> "Weekly"
        Repeat.MONTHLY -> "Monthly"
    }

    // Row containing the repeat component
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(enabled = isEdit, onClick = { updateExpanded?.let { updateExpanded() } }) // Enable click handling if in edit mode
    ) {
        // Column for the "Repeat:" label
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = "Repeat:", // Label text
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp
            )
        }

        // Row for displaying the literal repeat text and trailing icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            // Display the literal repeat text
            Text(
                text = literalRepeat,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )

            // If a trailing icon is provided, display it with some spacing
            if (trailingIcon != null) {
                Spacer(modifier = Modifier.size(10.dp))
                trailingIcon()
            }
        }
    }
}

@Composable
fun AttachmentItem(
    isDelegatedMember: Boolean,
    loggedInUserRole: Role,
    context: Context, // Android context
    taskId: String, // ID of the task to which the attachment belongs
    attachment: Attachment, // Attachment data
    removeAttachment: suspend (String, Attachment) -> Unit, // Function to remove the attachment
    downloadFileFromFirebase: suspend (String, Attachment, (File) -> Unit, (Exception) -> Unit) -> Unit,
    showLoading: Boolean,
    showDownloadLoading: String,
    setShowDownloadLoadingValue: (String) -> Unit
) {
    val scope = rememberCoroutineScope()

    // Row containing the attachment item
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .defaultMinSize(0.dp, 55.dp)
            .fillMaxSize()
            .clickable(enabled = showDownloadLoading.isBlank() && !showLoading) {
                scope.launch {
                    setShowDownloadLoadingValue(attachment.id)
                    downloadFileFromFirebase(
                        taskId,
                        attachment,
                        {
                            setShowDownloadLoadingValue("")
                            openDocument(context, it)
                        },
                        {
                            Log.e("Server Error", it.message.toString())
                            setShowDownloadLoadingValue("")
                            Toast
                                .makeText(
                                    context,
                                    "Unable to download attachment!",
                                    Toast.LENGTH_LONG
                                )
                                .show()
                        }
                    )
                }
            } // Click listener to open the attached document
    ) {
        // Row containing the attachment icon and name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxSize()
                .weight(7f)
                .padding(start = 14.dp)
        ) {
            // Draw a circle indicating the attachment type
            Canvas(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(6.dp)
            ) {
                val (w, h) = size
                val r = 1f * min(w, h)
                drawCircle(color = CollaborantColors.DarkBlue, radius = r, center = center)
            }

            // Column containing attachment name and size
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = attachment.name, // Attachment name
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Text(
                    text = getLiteralSize(attachment.size), // Human-readable size
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        // Column containing the remove attachment button
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1f)
        ) {
            if(isDelegatedMember || loggedInUserRole == Role.TEAM_MANAGER || loggedInUserRole == Role.SENIOR_MEMBER) {
                // IconButton to remove the attachment
                IconButton(
                    enabled = showDownloadLoading.isBlank() && !showLoading,
                    onClick = {
                        scope.launch {
                            removeAttachment(taskId, attachment)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "Remove Icon"
                    )
                }
            }
        }
    }

    if(showDownloadLoading == attachment.id) {
        LinearProgressIndicator(
            color = CollaborantColors.DarkBlue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AttachmentComponent(
    taskId: String,
    isDelegatedMember: Boolean,
    loggedInUserRole: Role,
    attachments: List<Attachment>,
    addAttachment: suspend (String, Attachment) -> Unit,
    removeAttachment: suspend (String, Attachment) -> Unit,
    downloadFileFromFirebase: suspend (String, Attachment, (File) -> Unit, (Exception) -> Unit) -> Unit,
    showLoading: Boolean,
    setShowLoadingValue: (Boolean) -> Unit,
    showDownloadLoading: String,
    setShowDownloadLoadingValue: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    // Accessing the current context
    val context = LocalContext.current
    // Creating a launcher for activity result to open documents
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri ->
            // Retrieving the MIME type of the selected document
            val mimeType = context.contentResolver.getType(uri)
            // Checking if the MIME type indicates an application or an image
            if (mimeType?.startsWith("application/") == true || mimeType?.startsWith("image/") == true) {
                // Getting information about the attachment
                getAttachmentInfo(context, uri)?.let { (name, size) ->
                    // Adding the attachment to the list
                    scope.launch {
                        setShowLoadingValue(true)
                        addAttachment(
                            taskId, Attachment(
                                id = "",
                                name = name,
                                type = mimeType,
                                size = size,
                                uri = uri,
                            )
                        )
                    }.invokeOnCompletion { setShowLoadingValue(false) }
                }
            }
        }
    }

    // Card containing the attachment component
    Card(
        modifier = Modifier
            .defaultMinSize(0.dp, 180.dp)
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CollaborantColors.CardBackGroundGray.copy(0.4f),
            contentColor = Color.Black
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CollaborantColors.CardBackGroundGray)
        ) {
            // Column for displaying the title "Attachments"
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 10.dp, start = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = "Attachments",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp
                )
            }

            // Column for displaying the attachment plus icon
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if(isDelegatedMember || loggedInUserRole == Role.TEAM_MANAGER || loggedInUserRole == Role.SENIOR_MEMBER) {
                    if(showLoading) {
                        CircularProgressIndicator(
                            color = CollaborantColors.DarkBlue,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(y = 12.dp, x = (-12).dp)
                        )
                    } else {
                        // IconButton for launching the document picker
                        IconButton(
                            enabled = showDownloadLoading.isBlank(),
                            onClick = { launcher.launch(arrayOf("application/*", "image/*")) }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.paper_plus),
                                contentDescription = "Attachment Plus Icon",
                                tint = CollaborantColors.DarkBlue
                            )
                        }
                    }
                }
            }
        }

        // Divider separating the title and attachments
        Divider(thickness = 1.dp, color = CollaborantColors.BorderGray)

        Column(modifier = Modifier.fillMaxSize()) {
            // Checking if there are no attachments
            if (attachments.isEmpty()) {
                // Displaying a message if there are no attachments
                Text(
                    text = "No attachments",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                // Looping through attachments and displaying each attachment item
                attachments.forEachIndexed { index, attachment ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Displaying the attachment item
                        AttachmentItem(
                            isDelegatedMember = isDelegatedMember,
                            loggedInUserRole = loggedInUserRole,
                            context = context,
                            taskId = taskId,
                            attachment = attachment,
                            removeAttachment = removeAttachment,
                            downloadFileFromFirebase = downloadFileFromFirebase,
                            showLoading = showLoading,
                            showDownloadLoading = showDownloadLoading,
                            setShowDownloadLoadingValue = setShowDownloadLoadingValue
                        )

                        // Adding a divider between attachment items
                        if (index != attachments.size - 1) {
                            Divider(
                                thickness = 1.dp,
                                color = CollaborantColors.BorderGray.copy(0.4f),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentItem(loggedInUserId: String, comment: Comment, users: List<User>) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .defaultMinSize(0.dp, 80.dp)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Row for displaying the comment author and timestamp
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Finding the user associated with the comment author ID
            users.find { it.id == comment.authorId }?.let { user ->
                // Box for displaying the user's profile image or monogram
                Box(modifier = Modifier
                    .size(36.dp)
                    .padding(2.dp)) {
                    // Displaying a monogram presentation if the image profile is empty
                    ImagePresentationComp(
                        first = user.first,
                        last = user.last,
                        imageProfile = user.imageProfile,
                        fontSize = 14.sp
                    )
                }

                // Column for displaying the user's name and timestamp
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    // Text for displaying the user's name
                    Text(
                        text = if(user.id == loggedInUserId) { "You" }
                            else { user.first.plus(" ").plus(user.last) },
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    // Text for displaying the timestamp using a function to get time ago
                    Text(
                        text = getTimeAgo(comment.date),
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        // Text for displaying the comment content
        Text(
            text = comment.content,
            fontFamily = interFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            // Applying padding to the comment content
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}

@Composable
fun CommentsComp(loggedInUserId: String, comments: List<Comment>, users: List<User>) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Text for indicating comments section
        Text(
            text = "Comments:",
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            // Applying padding to the comments section title
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Card for containing comments
        Card(
            // Applying modifiers for size and width
            modifier = Modifier
                .defaultMinSize(0.dp, 80.dp)
                .fillMaxWidth(),
            // Customizing colors and shape of the card
            colors = CardDefaults.cardColors(
                containerColor = CollaborantColors.CardBackGroundGray.copy(0.4f),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            // Looping through comments and displaying each comment item
            comments.sortedBy { it.date }.forEachIndexed { idx, comment ->
                // Displaying the comment item
                CommentItem(loggedInUserId = loggedInUserId, comment = comment, users = users)

                // Adding a divider between comment items
                if (idx < comments.size - 1) {
                    Divider(
                        thickness = 1.dp,
                        color = CollaborantColors.BorderGray.copy(0.4f),
                        // Applying padding to the divider
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }

            // Displaying a message if there are no comments
            if (comments.isEmpty()) {
                Text(
                    text = "No comments",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    // Applying padding to the message
                    modifier = Modifier.padding(top = 12.dp, start = 12.dp)
                )
            }
        }
    }
}

@Composable
fun CommentTextField(
    // Flag to determine if the layout should be horizontal or vertical
    isHorizontal: Boolean,
    // Current value of the text field
    value: String,
    // Callback to update the value of the text field
    updateValue: (String) -> Unit,
    // Task ID associated with the comment
    taskId: String,
    // Callback to add a comment
    addComment: suspend (String, Comment) -> Unit,
    loggedInUserId: String,
    // Modifier for styling and layout customization
    modifier: Modifier
) {
    val scope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            // Applying weight to the column to control its width
            modifier = Modifier.weight(if (isHorizontal) { 11f } else { 5f })
        ) {
            // Outlined text field for entering comments
            OutlinedTextField(
                // Value of the text field
                value = value,
                // Callback to update the value of the text field
                onValueChange = updateValue,
                // Placeholder text when the text field is empty
                placeholder = {
                    Text(
                        text = "Add a comment",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = CollaborantColors.BorderGray
                    )
                },
                // Trailing icon to clear the text field
                trailingIcon = {
                    if (value.isNotBlank())
                        IconButton(onClick = { updateValue("") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "Clear Icon",
                                tint = CollaborantColors.BorderGray
                            )
                        }
                },
                // Rounded corner shape for the text field
                shape = RoundedCornerShape(10.dp),
                // Text style for the text field
                textStyle = TextStyle(
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp, start = 18.dp, end = 4.dp),
                // Keyboard options for the text field
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Default,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = true,
                ),
                // Colors for the outlined text field
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CollaborantColors.BorderGray,
                    unfocusedBorderColor = CollaborantColors.BorderGray,
                    focusedContainerColor = CollaborantColors.CardBackGroundGray.copy(0.2f),
                    unfocusedContainerColor = CollaborantColors.CardBackGroundGray.copy(0.2f)
                ),
            )
        }

        // Column for the send button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            // Applying weight to the column to control its width
            modifier = Modifier
                .weight(1f)
        ) {
            // IconButton for sending the comment
            IconButton(
                onClick = {
                    // Check if the text field is not blank
                    if (value.isNotBlank()) {
                        scope.launch {
                            // Add the comment using the provided callback
                            addComment(taskId, Comment(
                                id = "",
                                content = value,
                                authorId = loggedInUserId,
                                date = LocalDateTime.now()
                            ))
                        }.invokeOnCompletion {
                            // Clear the text field by updating its value
                            updateValue("")
                        }
                    }
                },
                // Customizing the colors of the IconButton
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = CollaborantColors.Yellow,
                    contentColor = Color.White
                ),
                modifier = Modifier.size(50.dp)
            ) {
                // Icon for sending the comment
                Icon(
                    painter = painterResource(id = R.drawable.send_comment),
                    contentDescription = "Send Icon",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun MemberItem(
    user: User,
    role: Role,
    loggedInUserId: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val literalRole = when (role) {
        Role.TEAM_MANAGER -> "Team Manager"
        Role.SENIOR_MEMBER -> "Senior Member"
        Role.JUNIOR_MEMBER -> "Junior Member"
    }

    ListItem(
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
            ) {
                ImagePresentationComp(
                    first = user.first,
                    last = user.last,
                    imageProfile = user.imageProfile,
                    fontSize = 18.sp
                )
            }
        },
        trailingContent = trailingContent,
        headlineContent = {
            Column {
                Text(
                    text = if (loggedInUserId == user.id) "You" else "${user.first} ${user.last}",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    fontFamily = interFamily
                )

                Text(
                    text = literalRole,
                    fontWeight = FontWeight.Light,
                    fontSize = 14.sp,
                    fontFamily = interFamily
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = CollaborantColors.PageBackGroundGray
        ),
        modifier = modifier
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MembersBottomSheet(
    team: Team,
    users: List<User>,
    members: List<String>,
    loggedInUserId: String,
    setShowBottomSheetValue: (Boolean) -> Unit,
    navController: NavController
) {
    val bottomSheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { setShowBottomSheetValue(false) },
        containerColor = CollaborantColors.PageBackGroundGray,
        dragHandle = {
            // Drag handle for the bottom sheet
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BottomSheetDefaults.DragHandle()
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
                ) {
                    // Title and close button for the bottom sheet
                    Text(
                        text = "Delegated members",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = CollaborantColors.DarkBlue,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    IconButton(
                        onClick = {
                            // Close the bottom sheet when close button is clicked
                            coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                setShowBottomSheetValue(false)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "Close Icon",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        // Content of the bottom sheet
        Divider(thickness = 1.dp, color = CollaborantColors.BorderGray.copy(0.8f))
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            val sortedMembers = bringPairToHead(team.members.filter { members.contains(it.key) }.toList(), loggedInUserId)

            items(sortedMembers) {(memberId, role) ->
                // Display member items
                users.find { it.id == memberId }?.let { user ->
                    MemberItem(
                        user = user,
                        role = role as Role,
                        loggedInUserId = loggedInUserId,
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                        setShowBottomSheetValue(false)
                                        navController.navigate("viewIndividualStats/${team.id}/${memberId}")
                                    }
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.chart),
                                        contentDescription = "Chart Icon",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                if(memberId != loggedInUserId) {
                                    IconButton(onClick = {
                                        coroutineScope.launch { bottomSheetState.hide() }
                                            .invokeOnCompletion {
                                                setShowBottomSheetValue(false)
                                                navController.navigate("viewChat/${team.id}/${memberId}")
                                            }
                                    }) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.send),
                                            contentDescription = "Send Icon",
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}