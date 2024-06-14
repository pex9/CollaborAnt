package it.polito.lab5.gui.teamStats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.model.Task
import it.polito.lab5.model.TaskState
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily

private const val infoText = "No Chart available.\nNo tasks found for this team.\nCreate some tasks and try again."

@Composable
fun HorizontalTeamStatsPane(team: Team, tasks: List<Task>, navController: NavController, p: PaddingValues, membersList: List<User>) {
    val teamTasks = tasks.filter { it.teamId == team.id }
    val literalTotTasks = teamTasks.count()
    val literalTotCompletedTasks = teamTasks.count { it.state == TaskState.COMPLETED }
    val literalCompletionPercentage = Math.round(literalTotCompletedTasks.toFloat()/literalTotTasks.toFloat()*100f)
    val colors = MaterialTheme.colorScheme
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
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 15.dp),

                ) {
                TeamStatsCard(literalTotTasks, literalTotCompletedTasks, literalCompletionPercentage, true)
            }

            if(literalTotTasks != 0) {
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(start = 15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(width = 1.dp, color = colors.outline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 30.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Chart(
                            data = mapOf(
                                1f to "Total",
                                (literalTotCompletedTasks.toFloat() / literalTotTasks.toFloat()) to "Completed"
                            ),
                            maxValue = literalTotTasks
                        )
                    }
                }
            }
            else {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(start = 15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(width = 1.dp, color = colors.outline),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = infoText,
                            color = colors.outline,
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.sp,
                            lineHeight = 30.sp,
                            modifier = Modifier.padding(25.dp),
                        )
                    }
                }
            }
        }
        // INDIVIDUAL STATS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp)
        ) {
            TeamMembersRanking(membersList, team, navController)
        }
    }
}

@Composable
fun VerticalTeamStatsPane(
    team: Team,
    tasks: List<Task>,
    navController: NavController, // NavController for navigation
    p: PaddingValues, // Padding values for layout
    membersList: List<User>,
) {
    val literalTotTasks = tasks.size
    val literalTotCompletedTasks = tasks.filter{ it.state == TaskState.COMPLETED }.size
    val literalCompletionPercentage = Math.round(literalTotCompletedTasks.toFloat() / literalTotTasks.toFloat() * 100f)
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = p.calculateTopPadding())
            .verticalScroll(rememberScrollState())
    ) {

        // CARD1
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 10.dp)
                .padding(top = 10.dp)
                .fillMaxWidth()
                .aspectRatio(1.4f),
        ) {
            Text(
                text = "Team Stats",
                overflow = TextOverflow.Ellipsis,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp,
                color = colors.onBackground
            )
            Spacer(modifier = Modifier.height(5.dp))
            TeamStatsCard(literalTotTasks, literalTotCompletedTasks, literalCompletionPercentage, false)
        }

        if (literalTotCompletedTasks != 0) {
        // CHARTS
        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 10.dp)
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
            Spacer(modifier = Modifier.height(5.dp))
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(width = 1.dp, color = Color.Gray),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val completed = if (literalTotTasks == 0 && literalTotCompletedTasks == 0) {
                            0f
                        } else {
                            literalTotCompletedTasks.toFloat() / literalTotTasks.toFloat()
                        }

                        Chart(
                            data = mapOf(
                                1f to "Total",
                                completed to "Completed"
                            ),
                            maxValue = literalTotTasks
                        )
                    }
                }
            }
        }
        else {
            Column(
                modifier = Modifier
                    .padding(horizontal = 30.dp, vertical = 10.dp)
                    .fillMaxWidth(),
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
                Spacer(modifier = Modifier.height(5.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    ),
                    border = BorderStroke(width = 1.dp, color = Color.Gray),
                ) {
                    Text(
                        text = infoText,
                        color = colors.outline,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(25.dp),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.sp,
                        lineHeight = 30.sp
                    )
                }
            }
        }

        // INDIVIDUAL STATS
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 30.dp)
        ) {
            TeamMembersRanking(membersList, team, navController)
        }
    }
}