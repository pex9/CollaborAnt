package it.polito.lab5.gui.myChats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyChatsTopBar() {
    // Get color scheme from MaterialTheme
    val colors = MaterialTheme.colorScheme

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.onSecondary,
            titleContentColor = colors.onPrimary,
        ),
        title = {
            Text(
                text = "CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CollaborantColors.DarkBlue,
                            CollaborantColors.Yellow
                        ) // Gradient colors
                    )
                )
            )
        }
    )
}

@Composable
fun MyChatsPage(
    loggedInUserId: String,
    userTeams: List<Team>,
    isReadState: List<Pair<String, Boolean>>,
    resetUnreadMessage: suspend (Team, String) -> Unit,
    navController: NavController, // NavController for navigation
    paddingValues: PaddingValues // Padding values for layout
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues), // Apply padding
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        item {
            Text(
                text = "MyChats",
                modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                letterSpacing = 0.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        itemsIndexed(userTeams.sortedBy { team -> team.chat.maxOfOrNull { it.date }  }.reversed()){ idx, team ->
            ChatItem(
                team = team,
                loggedInUserId = loggedInUserId,
                isReadState = isReadState,
                resetUnreadMessage = resetUnreadMessage,
                navController = navController
            )

            if(idx < userTeams.size - 1) {
                Divider(
                    thickness = 1.dp,
                    color = CollaborantColors.BorderGray.copy(0.4f)
                )
            }
        }
    }
}