package it.polito.lab5.gui.taskHistory

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.Action
import it.polito.lab5.gui.TaskStateComp
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min

@Composable
fun HistoryCanvas(isFirst: Boolean, isLast: Boolean) {
    val colors = MaterialTheme.colorScheme

    // Draw a line at the top of the canvas if it's not the first item
    Canvas(modifier = Modifier.height(53.dp)) {
        drawLine(
            color = if (isFirst) Color.Transparent else colors.secondaryContainer,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 5f
        )
    }
    // Add spacing
    Spacer(modifier = Modifier.height(8.dp))
    // Draw a circle in the middle of the canvas
    Canvas(modifier = Modifier.size(8.dp)) {
        val (w, h) = size
        val r = 1f * min(w, h)
        drawCircle(color = colors.secondaryContainer, radius = r, center = center)
    }
    // Add spacing
    Spacer(modifier = Modifier.height(8.dp))
    // Draw a line at the bottom of the canvas if it's not the last item
    Canvas(modifier = Modifier.height(53.dp)) {
        drawLine(
            color = if (isLast) Color.Transparent else colors.secondaryContainer,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            strokeWidth = 5f
        )
    }
}

@Composable
fun HistoryItem(action: Action, users: List<User>) {
    val colors = MaterialTheme.colorScheme
    Card(
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onBackground
        ),
        modifier = Modifier
            .fillMaxSize()
            .height(130.dp)
            .padding(top = 15.dp, bottom = 15.dp, end = 20.dp),
        border = BorderStroke(width = 1.dp, color = colors.outline),
    ) {
        Row(modifier = Modifier.weight(2f)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            ) {
                // Action description
                Text(
                    text = action.description,
                    modifier = Modifier.padding(8.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, end = 8.dp)
            ) {
                // Task status
                TaskStateComp(state = action.taskState, fontSize = 12.sp)
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
            val text = when(action.taskState) {
                TaskState.NOT_ASSIGNED -> "Created by"
                TaskState.PENDING -> "Delegated by"
                else -> "Updated by"
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = text,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Action author
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                    ) {
                        users.find { it.id == action.memberId }?.let { user ->
                            ImagePresentationComp(
                                first = user.first,
                                last = user.last,
                                imageProfile = user.imageProfile,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // Action date
                Text(
                    text = action.date.format(formatter),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun HistoryRow(action: Action, users: List<User>, isFirst: Boolean, isLast: Boolean) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            // Draw canvas representing the history item position
            HistoryCanvas(isFirst = isFirst, isLast = isLast)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(6f)
        ) {
            // Display history item details
            HistoryItem(action = action, users = users)
        }
    }
}
