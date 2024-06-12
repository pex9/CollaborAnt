package it.polito.lab5.gui.myChats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.interFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyChatsTopBar() {
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary


    val gradientColors =
        if(LocalTheme.current.isDark)
            listOf(
                colors.secondary,
                colors.primary,
            )
        else
            listOf(
                colors.onSurface,
                colors.secondaryContainer,
            )

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
        ),
        title = {
            Text(
                text = "My Chats",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
        },
        navigationIcon = {
            Text(
                text = "CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = gradientColors // Gradient colors
                    )
                ),
                modifier = Modifier.padding(start = 5.dp)
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
            .padding(paddingValues),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        item { Spacer(modifier = Modifier.height(20.dp)) }

        items(userTeams.sortedBy { team -> team.chat.maxOfOrNull { it.date } }.reversed()) {team ->
            ChatItem(
                team = team,
                loggedInUserId = loggedInUserId,
                isReadState = isReadState,
                resetUnreadMessage = resetUnreadMessage,
                navController = navController
            )
        }

    }
}