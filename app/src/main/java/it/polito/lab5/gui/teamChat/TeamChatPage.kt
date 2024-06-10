package it.polito.lab5.gui.teamChat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.DataBase
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamChatTopAppBar(team: Team, navController: NavController) {
    val (first, last) = getMonogramText(team.name)
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary

    // Center aligned top app bar with title and navigation icon
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colors.onBackground,
        ),
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { navController.navigate("infoTeam/${team.id}") }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(4.dp)
                ) {
                    ImagePresentationComp(
                        first = first,
                        last = last,
                        imageProfile = team.image,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    team.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = { navController.popBackStack() },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon"
                )
            }
        }
    )
}

@Composable
fun TeamChatPage (team: Team, users: List<User>, paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(rememberScrollState(), reverseScrolling = true)
    ) {
        val sortedTeamChat = team.chat.sortedBy { it.date }
        var previousDate = sortedTeamChat.first().date.minusDays(1).toLocalDate()

        sortedTeamChat.forEach { message ->
            if(message.receiverId == DataBase.LOGGED_IN_USER_ID || message.senderId == DataBase.LOGGED_IN_USER_ID || message.receiverId == null) {
                if(message.date.toLocalDate() != previousDate) {
                    DateCanvas(date = message.date.toLocalDate())
                    previousDate = message.date.toLocalDate()
                }

                if(message.senderId == DataBase.LOGGED_IN_USER_ID) {
                    MessageTo(message = message, users = users)
                } else {
                    MessageFrom(message = message, users = users)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}