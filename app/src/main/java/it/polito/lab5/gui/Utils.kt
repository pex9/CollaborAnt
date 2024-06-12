package it.polito.lab5.gui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.R
import it.polito.lab5.model.Empty
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.Taken
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.Uploaded
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlin.math.min
import coil.compose.AsyncImage
import it.polito.lab5.model.Option

@Composable
fun MonogramPresentationComp(
    first: String, // First name
    last: String, // Last name
    fontSize: TextUnit, // Font size for the monogram
    color: Color // Color for the monogram circle
) {
    var monogramText = "  "
    val textMeasurer = rememberTextMeasurer()
    val os = CollaborantColors.BorderGray

    // Construct monogram text if both first and last names are not blank
    if (first.isNotEmpty() && last.isNotEmpty())
        monogramText = "${first.first().uppercase()}${last.first().uppercase()}"

    // Draw the monogram on a Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
        val (w, h) = size
        val r = 0.5f * min(w, h) // Radius of the circle
        val textLayoutResult: TextLayoutResult =
            textMeasurer.measure(
                text = AnnotatedString(monogramText),
                style = TextStyle(color = Color.Black, fontSize = fontSize)
            )
        val textSize = textLayoutResult.size

        // Draw the monogram circle
        drawCircle(color = color, radius = r, center = center)
        drawCircle(color = os, radius = r, center = center, style = Stroke(width = 3f))
        // Draw the monogram text
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                (this.size.width - textSize.width) / 2f,
                (this.size.height - textSize.height) / 2f
            )
        )
    }
}

@Composable
fun ImagePresentationComp(first: String, last: String, imageProfile: ImageProfile, fontSize: TextUnit){
    val os = CollaborantColors.BorderGray

    when(imageProfile) {
        is Taken ->
            Image(
                bitmap = imageProfile.image.asImageBitmap(),
                contentScale = ContentScale.Crop,
                contentDescription = "Image Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, os, CircleShape)
            )
        is Uploaded ->
            AsyncImage(
                model = imageProfile.image,
                placeholder = painterResource(id = R.drawable.profile_placeholder),
                contentScale = ContentScale.Crop,
                contentDescription = "Image Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, os, CircleShape)
            )
        is Empty -> MonogramPresentationComp(first, last, fontSize, imageProfile.color)
    }
}

@Composable
fun TaskStateComp(
    state: TaskState, // Task state
    fontSize: TextUnit, // Font size for the task state text
    enabled: Boolean = false,
    onClick: (() -> Unit)? = null, // Click listener for the task state card
    trailingIcon: @Composable (() -> Unit?)? = null // Trailing icon for the task state card
) {
    val (literalState, color) = when (state) {
        TaskState.NOT_ASSIGNED -> "Not assigned" to CollaborantColors.DarkBlue
        TaskState.PENDING -> "Pending" to CollaborantColors.DarkBlue
        TaskState.IN_PROGRESS -> "In progress" to CollaborantColors.PriorityOrange
        TaskState.ON_HOLD -> "On-hold" to CollaborantColors.PriorityOrange
        TaskState.COMPLETED -> "Completed" to CollaborantColors.PriorityGreen
        TaskState.OVERDUE -> "Overdue" to CollaborantColors.PriorityOrange2
    }
    val modifier = if (onClick != null) Modifier.clickable(enabled = enabled) { onClick() }
    else Modifier

    // Draw the task state card
    Card(
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color,
        ),
        border = BorderStroke(width = 2.dp, color = color)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
        ) {
            // Display the task state text
            Text(
                text = literalState,
                color = color,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = fontSize,
                modifier = Modifier.padding(vertical = 2.dp, horizontal = 10.dp)
            )

            // Display the trailing icon if provided
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun TextFieldComp(
    value: String, // Current value of the text field
    updateValue: (String) -> Unit, // Callback function to update the value
    errorMsg: String, // Error message to display (if any)
    label: String, // Label for the text field
    numLines: Int, // Number of lines in the text field
    options: KeyboardOptions, // Keyboard options for the text field
    maxChars: Int = -1, // Maximum number of characters allowed (default: unlimited)
    leadingIcon: @Composable (() -> Unit)? = null // Leading icon (if any)
) {
    // Define colors for the text field
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedLabelColor = MaterialTheme.colorScheme.secondary,
        unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
        focusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
        errorBorderColor = CollaborantColors.PriorityRed,
        errorLabelColor = CollaborantColors.PriorityRed,
        errorSupportingTextColor = CollaborantColors.PriorityRed
    )

    // Create the OutlinedTextField composable
    OutlinedTextField(
        value = value, // Current value of the text field
        onValueChange = { newValue -> // Callback when the value changes
            if (maxChars > 0) { // Check if there's a maximum character limit
                if (newValue.length <= maxChars) // Ensure the new value doesn't exceed the limit
                    updateValue(newValue) // Update the value
            } else {
                updateValue(newValue) // Update the value without any character limit
            }
        },
        label = { // Label for the text field
            Text(
                text = label,
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            )
        },
        placeholder = { // Placeholder text for the text field
            Text(
                text = "Insert ".plus(label.lowercase()),
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = CollaborantColors.BorderGray
            )
        },
        isError = errorMsg.isNotBlank(), // Check if there's an error message to display
        leadingIcon = leadingIcon, // Leading icon for the text field
        trailingIcon = { // Trailing icon for the text field
            if (value.isNotBlank()) { // Show the clear icon if the value is not blank
                IconButton(onClick = { updateValue("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.cross),
                        contentDescription = "Clear Icon",
                        tint = CollaborantColors.BorderGray
                    )
                }
            }
        },
        maxLines = numLines, // Set the maximum number of lines for the text field
        shape = RoundedCornerShape(10.dp), // Rounded corner shape for the text field
        colors = fieldColors, // Apply the defined colors
        keyboardOptions = options, // Set the keyboard options for the text field
        textStyle = TextStyle( // Text style for the text field
            fontFamily = interFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        supportingText = { // Supporting text (e.g., error message, character count)
            Row {
                if (errorMsg.isNotBlank()) { // Display error message if it's not blank
                    Text(
                        text = errorMsg,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.weight(2f)
                    )
                }

                if (maxChars > 0) { // Display character count if there's a maximum character limit
                    Text(
                        text = "${value.length} / $maxChars",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth() // Set the modifier to fill the maximum width available
    )
}

@Composable
fun TagCircleComp(color: Color, modifier: Modifier) {
    Canvas(
        modifier = modifier
    ) {
        val (w, h) = size // Get the width and height of the canvas
        val r = 1f * min(w, h) // Calculate the radius as half of the smaller dimension
        drawCircle(color = color, radius = r, center = center) // Draw the circle with the specified color
        drawCircle(
            color = CollaborantColors.BorderGray, // Black color for the outline
            radius = r, // Same radius as the main circle
            center = center, // Center of the canvas
            style = Stroke(width = 1f) // Stroke style for the outline
        )
    }
}

@Composable
fun TextComp(text: String, label: String, minHeight: Dp, modifier: Modifier) {
    // Column to organize the label and text content vertically
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth() // Occupy the entire available width
    ) {
        // Label text displayed with a semi-bold font
        Text(
            text = label,
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp) // Add bottom padding for separation
        )

        // Card containing the text content with rounded corners
        Card(
            modifier = Modifier
                .defaultMinSize(0.dp, minHeight) // Set a minimum height for the card
                .fillMaxWidth(), // Occupy the entire available width
            colors = CardDefaults.cardColors(
                containerColor = CollaborantColors.CardBackGroundGray, // Background color of the card
                contentColor = Color.Black // Text color inside the card
            ),
            shape = RoundedCornerShape(16.dp), // Rounded corners for the card
        ) {
            // Text content displayed inside the card with a light font weight
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Light,
                fontSize = 16.sp,
                modifier = modifier // Apply the modifier passed to customize the text appearance
            )
        }
    }
}

@Composable
fun DialogComp(
    title: String,
    text: String,
    onConfirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        },
        text = {
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = CollaborantColors.PriorityRed)
            ) {
                Text(
                    text = onConfirmText,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = CollaborantColors.DarkBlue,
        textContentColor = CollaborantColors.BorderGray,
    )
}

@Composable
fun RepeatDialogComp(
    title: String,
    text: String,
    onConfirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    optionSelected: Option,
    setOptionSelectedValue: (Option) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = title,
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        },
        text = {
            LazyColumn {
                item {
                    Text(
                        text = text,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp
                    )
                }

                items(Option.entries) { option ->
                    val literalOption = when (option) {
                        Option.CURRENT -> "This task"
                        Option.ALL -> "All tasks"
                        Option.AFTER -> "This task and all next"
                    }

                    ListItem(
                        headlineContent = { Text(text = literalOption) },
                        leadingContent = {
                            RadioButton(
                                selected = optionSelected == option,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CollaborantColors.DarkBlue,
                                    unselectedColor = CollaborantColors.DarkBlue
                                )
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.selectable(
                            selected = optionSelected == option,
                            onClick = { setOptionSelectedValue(option) }
                        )
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = "Cancel",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = CollaborantColors.PriorityRed)
            ) {
                Text(
                    text = onConfirmText,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        },
        containerColor = Color.White,
        titleContentColor = CollaborantColors.DarkBlue,
        textContentColor = CollaborantColors.BorderGray,
    )
}


fun bringPairToHead(list: List<Pair<String, Any>>, targetId: String): List<Pair<String, Any>> {
    val indexOfTarget = list.indexOfFirst { it.first == targetId }

    return if (indexOfTarget != -1) {
        val targetPair = list[indexOfTarget]
        list.toMutableList().apply {
            removeAt(indexOfTarget)
            add(0, targetPair)
        }
    } else { list }
}