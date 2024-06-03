package it.polito.lab5.gui.teamStats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily

@Composable
fun HorizontalTeamStatsPane(teams: List<Team>, tasks: List<Task>, navController: NavController, p: PaddingValues, membersList: List<User>, teamId: Int) {
    val teamTasks = tasks.filter { it.teamId == teamId }
    val literalTotTasks = teamTasks.count()
    val literalTotCompletedTasks = teamTasks.count { it.state == TaskState.COMPLETED }
    val literalCompletionPercentage = Math.round(literalTotCompletedTasks.toFloat()/literalTotTasks.toFloat()*100f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = p.calculateTopPadding())
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(start = 40.dp, end = 40.dp, top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.wrapContentHeight().fillMaxWidth().weight(1f).padding(end = 15.dp),

                ) {
                TeamStatsCard(literalTotTasks, literalTotCompletedTasks, literalCompletionPercentage, true)
            }

            Card(
                modifier = Modifier.wrapContentHeight().fillMaxWidth().weight(1f).padding(start = 15.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                border = BorderStroke(width = 1.dp, color = Color.Gray),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize().padding(start = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Chart(
                        data = mapOf(
                            1f to "Total",
                            (literalTotCompletedTasks.toFloat()/literalTotTasks.toFloat()) to "Completed"
                        ),
                        maxValue = literalTotTasks
                    )
                }
            }
        }
        // INDIVIDUAL STATS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp)
        ) {
            TeamMembersRanking(membersList, teams, navController, teamId)
        }
    }
}

@Composable
fun VerticalTeamStatsPane(
    teams: List<Team>,
    tasks: List<Task>,
    navController: NavController, // NavController for navigation
    p: PaddingValues, // Padding values for layout
    membersList: List<User>,
    teamId: Int,
)
{
    val teamTasks = tasks.filter { it.teamId == teamId }
    val literalTotTasks = teamTasks.count()
    val literalTotCompletedTasks = teamTasks.count { it.state == TaskState.COMPLETED }
    val literalCompletionPercentage = Math.round(literalTotCompletedTasks.toFloat()/literalTotTasks.toFloat()*100f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = p.calculateTopPadding())
            .verticalScroll(rememberScrollState())
    ) {

        // CARD1
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 10.dp)
                .fillMaxWidth()
                .aspectRatio(1.4f),
        ) {
            Text(
                text = "Team Stats",
                overflow = TextOverflow.Ellipsis,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            )
            TeamStatsCard(literalTotTasks, literalTotCompletedTasks, literalCompletionPercentage, false)
        }

        // CHARTS
        Column(
            modifier = Modifier
                .padding(horizontal = 40.dp, vertical = 10.dp)
                .fillMaxWidth()
                .aspectRatio(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Charts",
                overflow = TextOverflow.Ellipsis,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            )
            Card(
                modifier = Modifier.fillMaxSize(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                border = BorderStroke(width = 1.dp, color = Color.Gray),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Chart(
                        data = mapOf(
                            1f to "Total",
                            (literalTotCompletedTasks.toFloat()/literalTotTasks.toFloat()) to "Completed"
                        ),
                        maxValue = literalTotTasks
                    )
                }
            }
        }

        // INDIVIDUAL STATS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp)
        ) {
            TeamMembersRanking(membersList, teams, navController, teamId)
        }
    }
}