package it.polito.lab5.gui.teamChat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.taskView.getTimeAgo
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Message
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MessageTextField(
    // Flag to determine if the layout should be horizontal or vertical
    isHorizontal: Boolean,
    // Current value of the text field
    value: String,
    // Callback to update the value of the text field
    updateValue: (String) -> Unit,
    // Task ID associated with the comment
    taskId: Int,
    // Callback to add a comment
    addMessage: (Int, Message) -> Unit,
    // Modifier for styling and layout customization
    newMessageReceiver: Int?,
    setIsReadState: (Int, Boolean) -> Unit,
    teamId: Int
) {
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceColorAtElevation(10.dp))
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
                        text = "Message",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = colors.outline
                    )
                },
                // Trailing icon to clear the text field
                trailingIcon = {
                    if (value.isNotBlank())
                        IconButton(onClick = { updateValue("") }) {
                            Icon(
                                painter = painterResource(id = R.drawable.cross),
                                contentDescription = "Clear Icon",
                                tint = colors.outline
                            )
                        }
                },
                // Rounded corner shape for the text field
                shape = RoundedCornerShape(10.dp),
                // Text style for the text field
                textStyle = TextStyle(
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
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
                    focusedBorderColor = colors.outline,
                    unfocusedBorderColor = colors.outline,
                    focusedContainerColor = colors.surfaceColorAtElevation(20.dp),
                    unfocusedContainerColor = colors.surfaceColorAtElevation(10.dp),
                ),
                maxLines = 6
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
            val containerColor = if(LocalTheme.current.isDark) colors.secondary else colors.primary
            // IconButton for sending the comment
            IconButton(
                onClick = {
                    // Check if the text field is not blank
                    if (value.isNotBlank()) {
                        // Add the comment using the provided callback
                        addMessage(
                            taskId,
                            Message(
                                content = value,
                                senderId = DataBase.LOGGED_IN_USER_ID,
                                date = LocalDateTime.now(),
                                receiverId = newMessageReceiver
                            )
                        )
                        // Clear the text field by updating its value
                        updateValue("")
                        setIsReadState(teamId, false)
                    }
                },
                // Customizing the colors of the IconButton
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = containerColor,
                    contentColor = colors.background
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
fun MessageTo(message: Message, users: List<User>) {
    val colors = MaterialTheme.colorScheme
    val receiver = users.find { it.id == message.receiverId }
    val directMessageInfo = if(receiver != null) "(Direct Message)" else ""
    val senderName = "You"
    val receiveName = when(receiver?.id) {
        null -> "Everybody"
        else -> receiver.first + " " + receiver.last
    }

    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 70.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)

    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.secondary.copy(0.2f),
                contentColor = colors.onBackground
            ),
            shape = CardDefaults.outlinedShape,
            //elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(width = 1.dp, color = colors.outline.copy(0.4f)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 6.dp)
            ) {
                Text(
                    text = "$senderName to $receiveName",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = colors.secondaryContainer
                )

                Spacer(modifier = Modifier.width(3.dp))

                if(receiver != null) {
                    Text(
                        text = directMessageInfo,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message.content,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)

            ) {
                Text(
                    text = getTimeAgo(message.date),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = colors.outline
                )
            }
        }
    }
}

@Composable
fun MessageFrom(message: Message, users: List<User>){
    val colors = MaterialTheme.colorScheme
    val sender = users.find { it.id == message.senderId }
    val receiver = users.find { it.id == message.receiverId }
    val directMessageInfo = if(receiver != null) "(Direct Message)" else ""
    val messageColor = colors.surface
    val senderName = sender?.first + " " + sender?.last
    val receiveName = when(receiver?.id) {
        null -> "Everybody"
        else -> "You"
    }

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 70.dp, top = 12.dp, bottom = 12.dp)
    ) {
        Box(modifier = Modifier.size(40.dp)){
            if (sender != null) {
                ImagePresentationComp(
                    first = sender.first,
                    last = sender.last,
                    fontSize = 13.sp,
                    imageProfile = sender.imageProfile
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = messageColor,
                contentColor = colors.onBackground
            ),
            elevation = CardDefaults.cardElevation(4.dp),
            border = BorderStroke(width = 1.dp, color = colors.outline.copy(0.4f)),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 6.dp)
            ) {
                Text(
                    text = "$senderName to $receiveName",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    color = colors.secondaryContainer
                )

                Spacer(modifier = Modifier.width(3.dp))

                if(receiver != null) {
                    Text(
                        text = directMessageInfo,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = CollaborantColors.Yellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = message.content,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(start = 10.dp, end = 10.dp, bottom = 6.dp)

            ) {
                Text(
                    text = getTimeAgo(message.date),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = CollaborantColors.BorderGray,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun ReceiverSelector(
    team: Team,
    users: List<User>,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    targetReceiver: Int?,
    setReceiverTargetValue: (Int?) -> Unit
) {
    val members = team.members.map { it.first }.toSet()
    val receiverList =
        users.filter { members.contains(it.id) && it.id != DataBase.LOGGED_IN_USER_ID }
    val colors = MaterialTheme.colorScheme
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.background)
            .padding(vertical = 5.dp)
    ) {
        val literalTargetReceiver = when (targetReceiver) {
            null -> "Everyone"
            else -> receiverList.find { it.id == targetReceiver }?.first.plus(" ")
                .plus(receiverList.find { it.id == targetReceiver }?.last)
        }
        val selectorColor = if(LocalTheme.current.isDark) colors.secondary else colors.primary
        Box(contentAlignment = Alignment.BottomCenter){
            Button(
                onClick = { setOptionsOpenedValue(true) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectorColor,
                    contentColor = colors.onSecondary
                ),
                modifier = Modifier.width(180.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = literalTargetReceiver,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = colors.onSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_down),
                        contentDescription = "Arrow Icon",
                    )
                }
            }

            Box {
                DropdownMenu(
                    expanded = optionsOpened,
                    onDismissRequest = { setOptionsOpenedValue(false) },
                    offset = DpOffset(x = (-28).dp, y = (4).dp),
                    modifier = Modifier
                        .background(colors.surfaceColorAtElevation(10.dp))
                ) {
                    Box(modifier = Modifier.size(180.dp, 200.dp)) {
                        LazyColumn {
                            item {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Everyone",
                                            fontFamily = interFamily,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            color = colors.onBackground
                                        )
                                    },
                                    trailingIcon = if (targetReceiver == null) {
                                        {
                                            Icon(
                                                painter = painterResource(id = R.drawable.check),
                                                contentDescription = "Check Icon",
                                                tint = colors.outline,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                    } else { null },
                                    onClick = { setOptionsOpenedValue(false); setReceiverTargetValue(null) },
                                )

                                Divider(
                                    thickness = 1.dp,
                                    color = colors.outline.copy(0.4f),
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                            }

                            itemsIndexed(receiverList) { idx, member ->
                                SelectorItem(
                                    member = member,
                                    targetReceiver = targetReceiver,
                                    setOptionsOpenedValue = setOptionsOpenedValue,
                                    setReceiverTargetValue = setReceiverTargetValue
                                )

                                if (idx < receiverList.size - 1) {
                                    Divider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(horizontal = 15.dp),
                                        color = colors.outline.copy(0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectorItem(
    member: User,
    targetReceiver: Int?,
    setOptionsOpenedValue: (Boolean) -> Unit,
    setReceiverTargetValue: (Int?) -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = member.first.plus(" ").plus(member.last),
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        trailingIcon = if(member.id == targetReceiver) {
                {
                   Icon(
                       painter = painterResource(id = R.drawable.check),
                       contentDescription = "Check Icon",
                       tint = CollaborantColors.BorderGray,
                       modifier = Modifier.size(13.dp)
                   )
                }
            } else { null },
        onClick = { setOptionsOpenedValue(false); setReceiverTargetValue(member.id) },
    )
}

@Composable
fun DateCanvas(date: LocalDate) {
    val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH)
    val literalDate = date.format(formatter)
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(40.dp)) {
        val textLayoutResult: TextLayoutResult =
            textMeasurer.measure(
                text = AnnotatedString(literalDate),
                style = TextStyle(
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = CollaborantColors.BorderGray,
                )
            )
        val textSize = textLayoutResult.size

        drawLine(
            color = CollaborantColors.BorderGray,
            start = Offset(24f, this.size.height / 2f),
            end = Offset(this.size.width / 2f - textSize.width / 2f - 20f, this.size.height / 2f),
            strokeWidth = 1f
        )

        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                (this.size.width - textSize.width) / 2f,
                (this.size.height - textSize.height) / 2f
            )
        )

        drawLine(
            color = CollaborantColors.BorderGray,
            start = Offset(this.size.width / 2f + textSize.width / 2f + 20f, this.size.height / 2f),
            end = Offset(this.size.width - 24f, this.size.height / 2f),
            strokeWidth = 1f
        )
    }
}
