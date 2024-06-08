package it.polito.lab5.gui.taskForm

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import it.polito.lab5.R
import it.polito.lab5.model.Repeat
import it.polito.lab5.model.Tag
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.gui.taskView.DelegatedMemberComp
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import java.time.LocalDate

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskFormTopBar(
    taskId: String?,
    navController: NavController,
    validate: () -> String,
    resetErrorMsg: (Boolean) -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
        ),
        title = {
            Text(
                text = if (taskId == null) { "New Task" } else { "Edit Task" },
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp
            )
        },
        navigationIcon = {
            TextButton(
                onClick = { resetErrorMsg(true) ; navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = CollaborantColors.DarkBlue
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon"
                )

                Text(
                    text = "Back",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            TextButton(
                onClick = {
                    val id = validate()
                    if(id.isNotBlank()) {
                        navController.popBackStack()
                        navController.navigate("viewTask/${id}")
                    } },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = CollaborantColors.DarkBlue
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Text(
                    text = if (taskId == null) "Create" else "Save",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
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
    parentId: String?,
    endRepeatDate : (LocalDate?),
    setEndRepeatDateValue: (LocalDate) -> Unit,
    endRepeatDateError: String,
    showEndRepeatField: Boolean,
    delegatedMembers: List<String>,
    addMember: (String) -> Unit,
    removeMember: (String) -> Unit,
    delegatedMembersError: String,
    repeat: Repeat,
    setRepeatValue: (Repeat) -> Unit,
    showTagMenu: Boolean,
    setShowTagMenuValue: (Boolean) -> Unit,
    showRepeatMenu: Boolean,
    setShowRepeatMenuValue: (Boolean) -> Unit,
    showDueDateDialog: Boolean,
    setShowDueDateDialogValue: (Boolean) -> Unit,
    showEndRepeatDateDialog: Boolean,
    setShowEndRepeatDateDialogValue: (Boolean) -> Unit,
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
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
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = CardDefaults.elevatedShape,
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
                    color = CollaborantColors.BorderGray.copy(0.4f),
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
                    color = CollaborantColors.BorderGray.copy(0.4f),
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
                    color = CollaborantColors.BorderGray.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                RepeatMenuComp(
                    repeat = repeat,
                    setRepeat = setRepeatValue,
                    showRepeatMenu = showRepeatMenu,
                    setShowRepeatMenuValue = setShowRepeatMenuValue,

                )
                AnimatedVisibility(
                    visible = showEndRepeatField,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {

                    Divider(
                        thickness = 1.dp,
                        color = CollaborantColors.BorderGray.copy(0.4f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        // Due date component
                        EndDatePickerComp(
                            date = endRepeatDate,
                            setDueDate = setEndRepeatDateValue,
                            showDueDateDialog = showEndRepeatDateDialog,
                            setShowDueDateDialogValue = setShowEndRepeatDateDialogValue
                        )
                    }
                }
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

    if(dueDateError.isNotBlank() || delegatedMembersError.isNotBlank() || endRepeatDateError.isNotBlank()) {
        val message = dueDateError.ifBlank { delegatedMembersError }.ifBlank { endRepeatDateError }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        resetErrorMsg()
    }
}