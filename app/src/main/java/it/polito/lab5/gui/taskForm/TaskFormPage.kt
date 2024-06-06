package it.polito.lab5.gui.taskForm

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.gui.taskView.DelegatedMemberComp
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily
import java.time.LocalDate

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskFormTopBar(
    taskId: Int?,
    navController: NavController,
    validate: () -> Int,
    resetErrorMsg: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colors.onBackground,
        ),
        title = {
            Text(
                text = if (taskId == null) { "New Task" } else { "Edit Task" },
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp,
                color = colors.onBackground
            )
        },
        navigationIcon = {
            TextButton(
                onClick = { resetErrorMsg(true) ; navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon",
                    tint = colors.onBackground
                )

                Text(
                    text = "Back",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.onBackground
                )
            }
        },
        actions = {
            TextButton(
                onClick = {
                    val id = validate()
                    if(id != -1) {
                        navController.popBackStack()
                        navController.navigate("viewTask/${id}")
                    } },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Text(
                    text = if (taskId == null) "Create" else "Save",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = colors.onBackground
                )
            }
        }
    )
}

@Composable
fun TaskFormPage(
    team: Team,
    users: List<User>,
    title: String,
    setTitleValue: (String) -> Unit,
    titleError: String,
    description: String,
    setDescriptionValue: (String) -> Unit,
    descriptionError: String,
    tag: Tag,
    setTagValue: (Tag) -> Unit,
    dueDate: LocalDate?,
    setDueDateValue: (LocalDate) -> Unit,
    dueDateError: String,
    delegatedMembers: List<Int>,
    addMember: (Int) -> Unit,
    removeMember: (Int) -> Unit,
    delegatedMembersError: String,
    repeat: Repeat,
    setRepeatValue: (Repeat) -> Unit,
    showTagMenu: Boolean,
    setShowTagMenuValue: (Boolean) -> Unit,
    showRepeatMenu: Boolean,
    setShowRepeatMenuValue: (Boolean) -> Unit,
    showDueDateDialog: Boolean,
    setShowDueDateDialogValue: (Boolean) -> Unit,
    showMemberBottomSheet: Boolean,
    setShowMemberBottomSheetValue: (Boolean) -> Unit,
    resetErrorMsg: () -> Unit,
    triState: ToggleableState,
    setTriStateValue: (ToggleableState) -> Unit,
    toggleTriState: () -> Unit
) {
    val context = LocalContext.current
    val defaultOpt = KeyboardOptions(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Text,
        capitalization = KeyboardCapitalization.Words,
        autoCorrect = false,
    )
    val descriptionOpt = defaultOpt.copy(
        imeAction = ImeAction.Done,
        capitalization = KeyboardCapitalization.Sentences,
        autoCorrect = true
    )
    val colors = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top= 10.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        TextFieldComp(
            value = title,
            updateValue = setTitleValue,
            errorMsg = titleError,
            label = "Task title",
            numLines = 1,
            options = defaultOpt,
            maxChars = 50
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.background, // White background for the card
                contentColor = colors.onBackground // Black text color for content
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Card elevation
            shape = CardDefaults.elevatedShape, // Rounded corner shape for the card
            border = BorderStroke(1.dp, colors.outline),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    // Tag component
                    TagMenuComp(
                        tag = tag,
                        setTag = setTagValue,
                        showTagMenu = showTagMenu,
                        setShowTagMenuValue = setShowTagMenuValue
                    )
                }

                Divider(
                    thickness = 1.dp,
                    color = colors.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    // Due date component
                    DatePickerComp(
                        date = dueDate,
                        setDueDate = setDueDateValue,
                        showDueDateDialog = showDueDateDialog,
                        setShowDueDateDialogValue = setShowDueDateDialogValue
                    )
                }

                Divider(
                    thickness = 1.dp,
                    color = colors.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                DelegatedMemberComp(
                    members = delegatedMembers,
                    users = users,
                    setShowBottomSheetValue = setShowMemberBottomSheetValue,
                    isEdit = true
                )

                Divider(
                    thickness = 1.dp,
                    color = colors.outline,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                RepeatMenuComp(
                    repeat = repeat,
                    setRepeat = setRepeatValue,
                    showRepeatMenu = showRepeatMenu,
                    setShowRepeatMenuValue = setShowRepeatMenuValue
                )
            }
        }

        //  Description Form
        TextFieldComp(
            value = description,
            updateValue = setDescriptionValue,
            errorMsg = descriptionError,
            label = "Description",
            numLines = 5,
            options = descriptionOpt,
            maxChars = 250
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (showMemberBottomSheet) {
            MembersPickerBottomSheet(
                team = team,
                users = users,
                members = delegatedMembers,
                setShowBottomSheetValue = setShowMemberBottomSheetValue,
                addMember = addMember,
                removeMember = removeMember,
                triState = triState,
                setTriStateValue = setTriStateValue,
                toggleTriState = toggleTriState
            )
        }
    }

    if(dueDateError.isNotBlank() || delegatedMembersError.isNotBlank()) {
        val message = dueDateError.ifBlank { delegatedMembersError }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        resetErrorMsg()
    }
}