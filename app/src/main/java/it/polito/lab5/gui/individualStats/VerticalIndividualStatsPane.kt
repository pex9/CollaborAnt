package it.polito.lab5.gui.individualStats

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.Role
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.interFamily
import it.polito.lab5.viewModels.IndividualStatsViewModel

@Composable
fun VerticalIndividualStatsPane(
    vm: IndividualStatsViewModel,
    navController: NavController,
    p: PaddingValues,
    targetMember: User,
    targetMemberRanking: Int,
    membersList: List<User>,
    teamId: Int,
) {
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .padding(top = p.calculateTopPadding())
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .padding(start = 35.dp, end = 35.dp, top = 20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(end = 5.dp)
            ) {
                ImagePresentationComp(
                    first = targetMember.first,
                    last = targetMember.last,
                    imageProfile = targetMember.imageProfile,
                    fontSize = 50.sp,
                )
            }

            Column(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxSize()
                    .padding(start = 10.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    targetMember.first + " " + targetMember.last,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp,
                    letterSpacing = 0.sp,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Text(
                    targetMember.email,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp
                )
                Text(
                    targetMember.telephone,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.sp
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .padding(horizontal = 35.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(end = 5.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    border = BorderStroke(width = 1.dp, color = colors.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Score",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                letterSpacing = 0.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .weight(4f)
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val points =
                                targetMember.kpiValues.find { it.first == teamId }?.second?.score
                                    ?: 0
                           val position = when(targetMemberRanking) {
                                1 -> R.drawable.first_place
                                2 -> R.drawable.second_place
                                3 -> R.drawable.third_place
                                else -> null
                            }

                            if(position != null){
                                Icon(
                                    painter = painterResource(id = position),
                                    contentDescription = "",
                                    modifier = Modifier.size(70.dp),
                                    tint = Color.Unspecified
                                )
                            }

                            Text(
                                points.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1.4f)
                    .fillMaxSize()
                    .padding(start = 5.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(bottom = 3.dp),
                    border = BorderStroke(width = 1.dp, color = colors.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    )
                ) {

                    val literalAssignedTask =
                        targetMember.kpiValues.find { it.first == teamId }?.second?.assignedTasks
                            ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Assigned Tasks",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                        Text(
                            literalAssignedTask.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(top = 3.dp),
                    border = BorderStroke(width = 1.dp, color = colors.outline),
                    colors = CardDefaults.cardColors(
                        containerColor = colors.surfaceColorAtElevation(10.dp),
                        contentColor = colors.onBackground
                    )
                ) {
                    val literalCompletedTask =
                        targetMember.kpiValues.find { it.first == teamId }?.second?.completedTasks
                            ?: 0
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Completed Tasks",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                        Text(
                            literalCompletedTask.toString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(3.2f)
                .padding(35.dp)
        ) {
            Text(
                text = "Team Members Ranking",
                overflow = TextOverflow.Ellipsis,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            )
            Card(
                modifier = Modifier
                    .fillMaxSize().padding(top = 10.dp),
                border = BorderStroke(width = 1.dp, color = colors.outline),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceColorAtElevation(10.dp),
                    contentColor = colors.onBackground
                )
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    membersList.forEachIndexed{ index, member ->
                        val itemModifier = if(index == 0) Modifier.padding(top = 8.dp) else Modifier.padding(top = 0.dp)
                        ListItem(
                            leadingContent = {
                                Row(
                                    modifier = Modifier.wrapContentSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = (membersList.indexOf(member) + 1).toString() + ".",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(50.dp)
                                    ) {
                                        //position of member in membersList + 1
                                        ImagePresentationComp(
                                            first = member.first,
                                            last = member.last,
                                            imageProfile = member.imageProfile,
                                            fontSize = 20.sp,
                                        )
                                    }
                                }
                            },
                            headlineContent = {
                                val role = when(vm.teams.collectAsState().value.find { it.id == teamId }?.members?.find { it.first == member.id }?.second){
                                    Role.TEAM_MANAGER -> "Team Manager"
                                    Role.SENIOR_MEMBER -> "Senior Member"
                                    Role.JUNIOR_MEMBER -> "Junior Member"
                                    null -> ""
                                }
                                Column {
                                    Text(
                                        text = "${member.first} ${member.last}",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = role,
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                    )
                                }

                            },
                            trailingContent = {
                                Text(
                                    text = "${member.kpiValues.find { it.first == teamId }?.second?.score}",
                                    fontFamily = interFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                )
                            },
                            modifier = itemModifier.clickable { navController.navigate("viewIndividualStats/${teamId}/${member.id}") },
                            colors = ListItemDefaults.colors(containerColor = colors.surfaceColorAtElevation(10.dp)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalIndividualStatsPane(
    vm: IndividualStatsViewModel,
    navController: NavController,
    p: PaddingValues,
    targetMember: User,
    targetMemberRanking: Int,
    membersList: List<User>,
    teamId: Int,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .padding(top = p.calculateTopPadding())
            .fillMaxSize()
            .background(colors.background)
            .padding(start = 35.dp, end = 35.dp, top = 20.dp, bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(end = 5.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(0.5f)
                        .aspectRatio(1f)
                        .padding(end = 5.dp)
                ) {
                    ImagePresentationComp(
                        first = targetMember.first,
                        last = targetMember.last,
                        imageProfile = targetMember.imageProfile,
                        fontSize = 40.sp,
                    )
                }
                /*-------------*/
                Column(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxSize()
                        .padding(start = 10.dp, bottom= 10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        targetMember.first + " " + targetMember.last,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        letterSpacing = 0.sp,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Text(
                        targetMember.email,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp
                    )
                    Text(
                        targetMember.telephone,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp
                    )
                }
            }
            /*-------------*/
            Row(
                modifier = Modifier
                    .fillMaxWidth().weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(end = 5.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxSize(),
                        border = BorderStroke(width = 1.dp, color = colors.outline),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceColorAtElevation(10.dp),
                            contentColor = colors.onBackground
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Score",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = interFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    letterSpacing = 0.sp
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .weight(4f)
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val points =
                                    targetMember.kpiValues.find { it.first == teamId }?.second?.score
                                        ?: 0
                                val position = when (targetMemberRanking) {
                                    1 -> R.drawable.first_place
                                    2 -> R.drawable.second_place
                                    3 -> R.drawable.third_place
                                    else -> null
                                }

                                if (position != null) {
                                    Icon(
                                        painter = painterResource(id = position),
                                        contentDescription = "",
                                        modifier = Modifier.size(70.dp),
                                        tint = Color.Unspecified
                                    )
                                }

                                Text(
                                    points.toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontFamily = interFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    letterSpacing = 0.sp
                                )
                            }
                        }
                    }
                }
                /*----------*/
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .padding(start = 5.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(bottom = 3.dp),
                        border = BorderStroke(width = 1.dp, color = colors.outline),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceColorAtElevation(10.dp),
                            contentColor = colors.onBackground
                        )
                    ) {

                        val literalAssignedTask =
                            targetMember.kpiValues.find { it.first == teamId }?.second?.assignedTasks
                                ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Assigned Tasks",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                            Text(
                                literalAssignedTask.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .padding(top = 3.dp),
                        border = BorderStroke(width = 1.dp, color = colors.outline),
                        colors = CardDefaults.cardColors(
                            containerColor = colors.surfaceColorAtElevation(10.dp),
                            contentColor = colors.onBackground
                        )
                    ) {
                        val literalCompletedTask =
                            targetMember.kpiValues.find { it.first == teamId }?.second?.completedTasks
                                ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Completed Tasks",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                            Text(
                                literalCompletedTask.toString(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontFamily = interFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = 0.sp
                            )
                        }
                    }
                }
            }
        }


        /*-----------*/
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(start = 5.dp)
        ) {
            Text(
                text = "Team Members Ranking",
                overflow = TextOverflow.Ellipsis,
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                letterSpacing = 0.sp
            )
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                border = BorderStroke(width = 1.dp, color = colors.outline),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceColorAtElevation(10.dp),
                    contentColor = colors.onBackground
                )
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    membersList.forEachIndexed { index, member ->
                        val itemModifier = if (index == 0) Modifier.padding(top = 8.dp) else Modifier.padding(top = 0.dp)
                        ListItem(
                            leadingContent = {
                                Row(
                                    modifier = Modifier.wrapContentSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = (membersList.indexOf(member) + 1).toString() + ".",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(50.dp)
                                    ) {
                                        //position of member in membersList + 1
                                        ImagePresentationComp(
                                            first = member.first,
                                            last = member.last,
                                            imageProfile = member.imageProfile,
                                            fontSize = 20.sp,
                                        )
                                    }
                                }
                            },
                            headlineContent = {
                                val role =
                                    when (vm.teams.collectAsState().value.find { it.id == teamId }?.members?.find { it.first == member.id }?.second) {
                                        Role.TEAM_MANAGER -> "Team Manager"
                                        Role.SENIOR_MEMBER -> "Senior Member"
                                        Role.JUNIOR_MEMBER -> "Junior Member"
                                        null -> ""
                                    }
                                Column {
                                    Text(
                                        text = "${member.first} ${member.last}",
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    )
                                    Text(
                                        text = role,
                                        fontFamily = interFamily,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp,
                                    )
                                }

                            },
                            trailingContent = {
                                Text(
                                    text = "${member.kpiValues.find { it.first == teamId }?.second?.score}",
                                    fontFamily = interFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                )
                            },
                            modifier = itemModifier.clickable { navController.navigate("viewIndividualStats/${teamId}/${member.id}") },
                            colors = ListItemDefaults.colors(containerColor = colors.surfaceColorAtElevation(10.dp)),
                        )
                    }
                }
            }
        }
    }
}