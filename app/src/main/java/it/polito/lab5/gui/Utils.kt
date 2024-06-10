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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
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
import it.polito.lab5.LocalTheme

@Composable
fun MonogramPresentationComp(
    first: String, // First name
    last: String, // Last name
    fontSize: TextUnit, // Font size for the monogram
    color: Color // Color for the monogram circle
) {
    var monogramText = "  "
    val textMeasurer = rememberTextMeasurer()
    val colors = MaterialTheme.colorScheme

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
        drawCircle(color = colors.outline, radius = r, center = center, style = Stroke(width = 3f))
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
    val colors = MaterialTheme.colorScheme

    when(imageProfile) {
        is Taken ->
            Image(
                bitmap = imageProfile.image.asImageBitmap(),
                contentScale = ContentScale.Crop,
                contentDescription = "Image Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, colors.outline, CircleShape)
            )
        is Uploaded ->
            AsyncImage(
                model = imageProfile.image,
                contentScale = ContentScale.Crop,
                contentDescription = "Image Profile",
                modifier = Modifier
                    .clip(CircleShape)
                    .border(1.dp, colors.outline, CircleShape)
            )
        is Empty -> MonogramPresentationComp(first, last, fontSize, imageProfile.color)
    }
}

@Composable
fun TaskStateComp(
    state: TaskState, // Task state
    fontSize: TextUnit, // Font size for the task state text
    onClick: (() -> Unit)? = null, // Click listener for the task state card
    trailingIcon: @Composable (() -> Unit?)? = null // Trailing icon for the task state card
) {

    val colors = MaterialTheme.colorScheme
    val customStateColor = if(LocalTheme.current.isDark) colors.primaryContainer else colors.onBackground
    val (literalState, color) = when (state) {
        TaskState.NOT_ASSIGNED -> "Not assigned" to colors.secondaryContainer
        TaskState.PENDING -> "Pending" to colors.secondaryContainer
        TaskState.IN_PROGRESS -> "In progress" to customStateColor
        TaskState.ON_HOLD -> "On-hold" to customStateColor
        TaskState.COMPLETED -> "Completed" to colors.onBackground
        TaskState.OVERDUE -> "Overdue" to customStateColor
    }
    val modifier = if (onClick != null) Modifier.clickable { onClick() }
    else Modifier

    // Draw the task state card
    Card(
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f),
            contentColor = color,
        ),
        border = BorderStroke(width = 1.dp, color = color),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = modifier
        ) {
            // Display the task state text
            Text(
                text = literalState,
                color = colors.onBackground,
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
    val colors = MaterialTheme.colorScheme
    // Define colors for the text field
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedLabelColor = colors.secondaryContainer,
        unfocusedLabelColor = colors.secondaryContainer,
        errorLabelColor = colors.error,


        focusedBorderColor = colors.outline,
        unfocusedBorderColor = colors.outline,
        errorBorderColor = colors.error,

        errorSupportingTextColor = colors.error,
        focusedSupportingTextColor= colors.secondaryContainer,
        unfocusedSupportingTextColor = colors.secondaryContainer,

        errorContainerColor = colors.surfaceColorAtElevation(10.dp),
        focusedContainerColor = colors.surfaceColorAtElevation(10.dp),
        unfocusedContainerColor = colors.surfaceColorAtElevation(10.dp),

        unfocusedPrefixColor = colors.secondaryContainer,
        unfocusedSuffixColor = colors.secondaryContainer,

        cursorColor = colors.secondaryContainer,
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
                color = colors.outline
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
                        tint = colors.outline
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
    val colors = MaterialTheme.colorScheme
    Canvas(
        modifier = modifier
    ) {
        val (w, h) = size // Get the width and height of the canvas
        val r = 1f * min(w, h) // Calculate the radius as half of the smaller dimension
        drawCircle(color = color, radius = r, center = center) // Draw the circle with the specified color
        drawCircle(
            color = colors.outline, // Black color for the outline
            radius = r, // Same radius as the main circle
            center = center, // Center of the canvas
            style = Stroke(width = 2f) // Stroke style for the outline
        )
    }
}

@Composable
fun TextComp(text: String, label: String, minHeight: Dp, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
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
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 4.dp), // Add bottom padding for separation
            color = colors.onBackground
        )

        // Card containing the text content with rounded corners
        Card(
            modifier = Modifier
                .defaultMinSize(0.dp, minHeight) // Set a minimum height for the card
                .fillMaxWidth(), // Occupy the entire available width
            colors = CardDefaults.cardColors(
                containerColor = colors.surface, // Background color of the card
                contentColor = colors.onBackground // Text color inside the card
            ),
            border = BorderStroke(1.dp, colors.outline),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            // Text content displayed inside the card with a light font weight
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                modifier = modifier, // Apply the modifier passed to customize the text appearance
                color = colors.onBackground
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
    val colors = MaterialTheme.colorScheme
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                color = colors.onBackground
            )
        },
        text = {
            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = colors.onBackground
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = colors.error)
            ) {
                Text(
                    text = onConfirmText,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = colors.onBackground
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
                    fontSize = 16.sp,
                    color = colors.onBackground
                )
            }
        },
        containerColor = colors.background,
        titleContentColor = CollaborantColors.DarkBlue,
        textContentColor = colors.outline,
    )
}

fun bringPairToHead(list: List<Pair<Int, Any>>, targetId: Int): List<Pair<Int, Any>> {
    val indexOfTarget = list.indexOfFirst { it.first == targetId }

    return if (indexOfTarget != -1) {
        val targetPair = list[indexOfTarget]
        list.toMutableList().apply {
            removeAt(indexOfTarget)
            add(0, targetPair)
        }
    } else { list }
}