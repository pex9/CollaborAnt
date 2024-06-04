package it.polito.lab5.gui.taskForm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.R
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.gui.taskView.DueDateComp
import it.polito.lab5.gui.taskView.MemberItem
import it.polito.lab5.gui.taskView.RepeatComponent
import it.polito.lab5.gui.taskView.TagComp
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Composable
fun TagMenuComp(
    tag: Tag, // Currently selected tag
    setTag: (Tag) -> Unit, // Callback to set the tag
    showTagMenu: Boolean, // Indicates whether the tag menu is shown or not
    setShowTagMenuValue: (Boolean) -> Unit // Callback to toggle the visibility of the tag menu
) {
    val colors = MaterialTheme.colorScheme
    // Box to contain the tag component and the tag menu
    Box(contentAlignment = Alignment.BottomEnd) {
        // Tag component with an arrow icon to indicate the tag menu state
        TagComp(
            tag = tag,
            updateExpanded ={ setShowTagMenuValue(!showTagMenu) }, // Toggle the visibility of the tag menu
            trailingIcon = {
                if (showTagMenu) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_up),
                        contentDescription = "Arrow Up Icon",
                        tint = CollaborantColors.DarkBlue
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_down),
                        contentDescription = "Arrow down Icon",
                        tint = CollaborantColors.DarkBlue
                    )
                }
            }
        )

        // Box to contain the tag menu
        Box {
            // Dropdown menu to display tag options
            DropdownMenu(
                expanded = showTagMenu,
                onDismissRequest = { setShowTagMenuValue(false) }, // Dismiss the tag menu when requested
                modifier = Modifier
                    .background(Color.White) // Background color of the tag menu
                    .width(135.dp), // Width of the tag menu
                offset = DpOffset(x = 8.dp, y = (-5).dp)
            ) {
                // Iterate over tag options
                Tag.entries.drop(1).forEachIndexed { idx, t ->
                    val literalTag = when (t) {
                        Tag.LOW -> "Low"
                        Tag.MEDIUM -> "Medium"
                        Tag.HIGH -> "High"
                        Tag.UNDEFINED -> ""
                    }

                    val offset = when (idx) {
                        0 -> { (-2).dp }
                        Tag.entries.size - 2 -> { 2.dp }
                        else -> { 0.dp }
                    }

                    // Dropdown menu item for each tag option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = literalTag,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        },
                        trailingIcon = {
                            if(t == tag) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check Icon",
                                    tint = colors.outline,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        },
                        onClick = { setShowTagMenuValue(false); setTag(t) }, // Set the selected tag
                        modifier = Modifier.offset(y = offset) // Offset for positioning the menu item
                    )

                    // Divider between menu items
                    if (idx != Tag.entries.size - 2)
                        Divider(
                            color = colors.outline,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DatePickerComp(
    date: LocalDate?, // Currently selected date
    setDueDate: (LocalDate) -> Unit, // Callback to set the due date
    showDueDateDialog: Boolean, // Indicates whether the date picker dialog is shown or not
    setShowDueDateDialogValue: (Boolean) -> Unit // Callback to toggle the visibility of the date picker dialog
) {
    // Remember the state of the date picker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
    )

    // Due date component with an arrow icon to toggle the date picker dialog
    DueDateComp(
        date = date,
        isEdit = true,
        updateVisible = { setShowDueDateDialogValue(!showDueDateDialog) })

    // Date picker dialog
    if (showDueDateDialog) {
        DatePickerDialog(
            onDismissRequest = {
                datePickerState.setSelection(
                    date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
                )
                setShowDueDateDialogValue(false)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.of("UTC"))
                        }?.toLocalDate()?.let { setDueDate(it) }
                        setShowDueDateDialogValue(false)
                    }
                ) {
                    Text(
                        text = "OK",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        datePickerState.setSelection(
                            date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
                        )
                        setShowDueDateDialogValue(false)
                    }
                ) {
                    Text(
                        text = "Cancel",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White),
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // Date picker component
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select due date",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.offset(x = 25.dp, y = 16.dp)
                    )
                },
                colors = DatePickerDefaults.colors(
                    titleContentColor = CollaborantColors.DarkBlue,
                    weekdayContentColor = CollaborantColors.DarkBlue
                ),
                dateValidator = {
                    // Date validator to allow only future dates
                    val selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    val currentDate = LocalDate.now()
                    selectedDate >= currentDate
                }
            )
        }
    }
}


@Composable
fun RepeatMenuComp(
    repeat: Repeat, // Selected repeat option
    setRepeat: (Repeat) -> Unit, // Callback to update the repeat option
    showRepeatMenu: Boolean, // Flag to indicate if the repeat menu is shown
    setShowRepeatMenuValue: (Boolean) -> Unit // Callback to toggle the visibility of the repeat menu
) {
    val colors = MaterialTheme.colorScheme
    // Box to contain the repeat menu
    Box(contentAlignment = Alignment.BottomEnd) {
        // Repeat component with toggle icon
        RepeatComponent(repeat, { setShowRepeatMenuValue(!showRepeatMenu) }) {
            // Icon to toggle the visibility of the repeat menu
            if (showRepeatMenu) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_up),
                    contentDescription = "Arrow Up Icon",
                    tint = CollaborantColors.DarkBlue
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = "Arrow down Icon",
                    tint = CollaborantColors.DarkBlue,
                )
            }
        }

        // Box to contain the repeat menu items
        Box {
            DropdownMenu(
                expanded = showRepeatMenu,
                onDismissRequest = { setShowRepeatMenuValue(false) },
                modifier = Modifier
                    .background(Color.White)
                    .width(150.dp),
                offset = DpOffset(x = 8.dp, y = (-5).dp)
            ) {
                // Iterate over repeat options and display each in the dropdown menu
                Repeat.entries.forEachIndexed { idx, r ->
                    val literalRepeat = when (r) {
                        Repeat.NEVER -> "Never"
                        Repeat.DAILY -> "Daily"
                        Repeat.WEEKLY -> "Weekly"
                        Repeat.MONTHLY -> "Monthly"
                    }

                    val offset = when (idx) {
                        0 -> { (-4).dp }
                        Repeat.entries.size - 1 -> { 4.dp }
                        else -> { 0.dp }
                    }

                    // Dropdown menu item for each repeat option
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = literalRepeat,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        },
                        trailingIcon = {
                            if(r == repeat) {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check Icon",
                                    tint = colors.outline,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        },
                        onClick = { setShowRepeatMenuValue(false); setRepeat(r) },
                        modifier = Modifier.offset(y = offset)
                    )

                    // Divider between menu items
                    if (idx != Tag.entries.size - 1)
                        Divider(
                            color = colors.outline,
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MembersPickerBottomSheet(
    team: Team,
    users: List<User>,
    members: List<Int>, // List of selected member IDs
    setShowBottomSheetValue: (Boolean) -> Unit, // Callback to toggle the visibility of the bottom sheet
    addMember: (Int) -> Unit, // Callback to add a member
    removeMember: (Int) -> Unit, // Callback to remove a member
    triState: ToggleableState,
    setTriStateValue: (ToggleableState) -> Unit,
    toggleTriState: () -> Unit
) {
    // Remember the state of the modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState()
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme

    // Modal bottom sheet for selecting members
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { setShowBottomSheetValue(false) }, // Dismiss the bottom sheet when requested
        containerColor = CollaborantColors.PageBackGroundGray, // Background color of the bottom sheet
        dragHandle = {
            // Drag handle for the bottom sheet
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Default drag handle
                BottomSheetDefaults.DragHandle()

                // Box for the title and close button
                Box(
                    contentAlignment = Alignment.TopEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "All:",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        )

                        TriStateCheckbox(
                            state = triState,
                            onClick = {
                                team.members.map { it.first }.forEach { memberId ->
                                    if(triState == ToggleableState.On) { removeMember(memberId) }
                                    else { addMember(memberId) }
                                }
                                toggleTriState()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CollaborantColors.MediumBlue40,
                                uncheckedColor = CollaborantColors.DarkBlue,
                                checkmarkColor = CollaborantColors.DarkBlue,
                                disabledIndeterminateColor = CollaborantColors.DarkBlue
                            )
                        )
                    }

                    // Title of the bottom sheet
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(
                            text = "Selected members", // Title text
                            fontFamily = interFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = CollaborantColors.DarkBlue // Text color
                        )

                        // Display the count of selected members
                        Text(
                            text = "${members.size}/${team.members.size}", // Member count
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }

                    // Close button to dismiss the bottom sheet
                    IconButton(
                        onClick = {
                            // Dismiss the bottom sheet
                            coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                setShowBottomSheetValue(false)
                            }
                        }
                    ) {
                        // Close icon
                        Icon(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "Close Icon",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxWidth() // Fill maximum width
    ) {
        // Divider between header and member list
        Divider(thickness = 1.dp, color = colors.outline)
        // Lazy column to display the list of members
        LazyColumn(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Iterate over the list of users and display each as a member item
            items(team.members.sortedBy { it.second }) {(memberId, role) ->
                // Display member items
                users.find { it.id == memberId }?.let { user ->
                    MemberItem(
                        user = user,
                        role = role,
                        trailingContent = {
                            Checkbox(
                                checked = members.contains((user.id)),
                                onCheckedChange = null,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CollaborantColors.MediumBlue40,
                                    uncheckedColor = CollaborantColors.DarkBlue,
                                    checkmarkColor = CollaborantColors.DarkBlue

                                )
                            )
                        },
                        modifier = Modifier.clickable {
                            if(members.contains(user.id)) { removeMember(user.id) }
                            else { addMember(user.id) }

                            when(members.size) {
                                0 -> setTriStateValue(ToggleableState.Off)
                                in 1 until team.members.size -> setTriStateValue(ToggleableState.Indeterminate)
                                team.members.size -> setTriStateValue(ToggleableState.On)
                            }
                        }
                    )
                }
            }
        }
    }
}
