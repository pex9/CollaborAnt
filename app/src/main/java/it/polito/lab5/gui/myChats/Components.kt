package it.polito.lab5.gui.myChats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch

@Composable
fun ChatItem(
    team: Team,
    loggedInUserId: String,
    isReadState: List<Pair<String, Boolean>>,
    resetUnreadMessage: suspend (Team, String) -> Unit,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val (first, last) = getMonogramText(team.name)
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
            contentColor = colors.onBackground
        ),
        border = BorderStroke(width = 1.dp, color = colors.outline),
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        ListItem(
            leadingContent = {
                Box(modifier = Modifier.size(50.dp)){
                    ImagePresentationComp(
                        first = first,
                        last = last,
                        imageProfile = team.image,
                        fontSize = 17.sp
                    )
                }
            },
            headlineContent = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    val lastMessage = team.chat.lastOrNull()?.content

                    Text(
                        text = team.name,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = colors.onBackground
                    )

                if (lastMessage != null) {
                    Text(
                        text = lastMessage,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(top= 4.dp),
                        color = colors.outline
                    )
                }
            }
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Top,
            ) {
                val formattedDate = dateFormatter(team.chat.lastOrNull()?.date)

                Text(
                    text = formattedDate ?: "",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    letterSpacing = 0.sp,
                    maxLines = 1,
                    color = colors.onBackground
                )

                Box(modifier = Modifier.padding(top = 14.dp)) {
                    val isReadFlag = isReadState.find { it.first == team.id }?.second ?: false
                    if(isReadFlag){
                        UnreadMessageComp()
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = colors.surface
        ),
        modifier = Modifier
            .height(80.dp)
            .clickable {
                scope.launch {
                    resetUnreadMessage(team, loggedInUserId)
                }.invokeOnCompletion { navController.navigate("viewChat/${team.id}/${null}") }
            }
    )
}

@Composable
fun UnreadMessageComp() {
    val color = MaterialTheme.colorScheme.secondaryContainer
    Canvas(
        modifier = Modifier.size(12.dp),
        onDraw = {
            // Draw inner circle with the determined tag container color
            drawCircle(color = color, radius = 15f)
        }
    )
}