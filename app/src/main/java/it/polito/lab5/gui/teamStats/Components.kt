package it.polito.lab5.gui.teamStats

import android.graphics.Paint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.Role
import it.polito.lab5.model.Team
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    value:Int,
    primaryColor: Color,
    secondaryColor:Color,
    maxValue:Int = 100,
    circleRadius:Float,
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }

    val positionValue by remember { mutableIntStateOf(value) }

    Box(
        modifier = modifier
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ){
            val width = size.width
            val height = size.height
            val circleThickness = width / 11f
            circleCenter = Offset(x = width/2f, y = height/2f)


            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        primaryColor.copy(0.45f),
                        secondaryColor.copy(0.15f)
                    )
                ),
                radius = circleRadius,
                center = circleCenter
            )


            drawCircle(
                style = Stroke(
                    width = circleThickness
                ),
                color = secondaryColor,
                radius = circleRadius,
                center = circleCenter
            )

            drawArc(
                color = primaryColor,
                startAngle = 0f,
                sweepAngle = -(360f/maxValue) * positionValue.toFloat(),
                style = Stroke(
                    width = circleThickness,
                    cap = StrokeCap.Round
                ),
                useCenter = false,
                size = Size(
                    width = circleRadius * 2f,
                    height = circleRadius * 2f
                ),
                topLeft = Offset(
                    (width - circleRadius * 2f)/2f,
                    (height - circleRadius * 2f)/2f
                )

            )

            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$positionValue %",
                        circleCenter.x,
                        circleCenter.y + 45.dp.toPx()/3f,
                        Paint().apply {
                            textSize = 30.sp.toPx()
                            textAlign = Paint.Align.CENTER
                            color = CollaborantColors.DarkBlue.toArgb()
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Chart(
    data: Map<Float, String>,
    maxValue: Int
) {

    // BarGraph Dimensions
    val barGraphHeight by remember { mutableStateOf(200.dp) }
    val barGraphWidth by remember { mutableStateOf(20.dp) }
    // Scale Dimensions
    val scaleYAxisWidth by remember { mutableStateOf(50.dp) }
    val scaleLineWidth by remember { mutableStateOf(2.dp) }

    Column(
        modifier = Modifier
            .padding(start= 30.dp,end = 50.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barGraphHeight),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            // scale Y-Axis
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(scaleYAxisWidth),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = maxValue.toString())
                    Spacer(modifier = Modifier.fillMaxHeight())
                }

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(text = (maxValue / 2).toString())
                    Spacer(modifier = Modifier.fillMaxHeight(0.5f))
                }

            }

            // Y-Axis Line
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(scaleLineWidth)
                    .background(Color.Black)
            )

            // graph
            Box(
                modifier = Modifier
                    .padding(start = barGraphWidth*1.5f, bottom = 5.dp)
                    .clip(CircleShape)
                    .width(barGraphWidth)
                    .fillMaxHeight(data.keys.first())
                    .background(CollaborantColors.DarkBlue)
                /*.clickable {
                    Toast
                        .makeText(context, it.key.toString(), Toast.LENGTH_SHORT)
                        .show()
                }*/
            )

            Box(
                modifier = Modifier
                    .padding(start = barGraphWidth*2.5f, bottom = 5.dp)
                    .clip(CircleShape)
                    .width(barGraphWidth)
                    .fillMaxHeight(data.keys.last())
                    .background(CollaborantColors.DarkBlue)
                /*.clickable {
                    Toast
                        .makeText(context, it.key.toString(), Toast.LENGTH_SHORT)
                        .show()
                }*/
            )
        }

        // X-Axis Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(scaleLineWidth)
                .background(Color.Black)
        )

        // Scale X-Axis
        Row(
            modifier = Modifier
                .padding(start = scaleYAxisWidth+barGraphWidth+scaleLineWidth)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(barGraphWidth)
        ) {
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = data.values.first(),
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = data.values.last(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTopBar(team: Team, navController: NavController) {
    val (first, last) = getMonogramText(team.name)
    val colors = MaterialTheme.colorScheme
    // Center aligned top app bar with title and navigation icon
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colors.onSecondary,
            titleContentColor = colors.onPrimary,
        ),
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
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
                    contentColor = CollaborantColors.DarkBlue
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon"
                )
            }
        },
    )
}

@Composable
fun TeamMembersRanking(membersList: List<User>, teams: List<Team>, navController: NavController, teamId: String){
    Text(
        text = "Team Members Ranking",
        overflow = TextOverflow.Ellipsis,
        fontFamily = interFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    )
    Card(
        modifier = Modifier.fillMaxSize().padding(top = 10.dp),
        border = BorderStroke(width = 1.dp, color = Color.Gray),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier.padding(bottom = 10.dp),
        ) {
            membersList.forEach{ member ->
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
                        val role = when(teams.find { it.id == teamId }?.members?.find { it.first == member.id }?.second){
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
                            text = "${member.kpiValues[teamId]?.score}",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                        )
                    },
                    modifier = Modifier.clickable { navController.navigate("viewIndividualStats/${teamId}/${member.id}") },
                    colors = ListItemDefaults.colors(containerColor = Color.White),
                )
            }
        }
    }
}

@Composable
fun TeamStatsCard(literalTotTasks: Int, literalTotCompletedTasks: Int, literalCompletionPercentage: Int, horizontal: Boolean){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        border = BorderStroke(width = 1.dp, color = Color.Gray),
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(start = 25.dp, bottom = 20.dp)
                    .weight(1f)
                    .fillMaxSize(),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val textModifier = if(horizontal) Modifier.padding(start = 10.dp, top = 40.dp, end = 10.dp, bottom = 0.dp) else Modifier.padding(10.dp)
                Text(
                    text = "Project Completion",
                    //modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center,
                    modifier = textModifier
                )

                val circleRadius = if(horizontal) 180f else 150f
                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .wrapContentHeight(),
                    value = literalCompletionPercentage,
                    primaryColor = CollaborantColors.DarkBlue,
                    secondaryColor = CollaborantColors.Yellow,
                    circleRadius = circleRadius,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp).weight(1f),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Total Tasks",
                            //modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            letterSpacing = 0.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = literalTotTasks.toString(),
                            //modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                            fontFamily = interFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                        .wrapContentHeight(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Completed Tasks",
                            //modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp,
                            letterSpacing = 0.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = literalTotCompletedTasks.toString(),
                            //modifier = Modifier.padding(start= 20.dp, top = 22.dp),
                            fontFamily = interFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            letterSpacing = 0.sp
                        )
                    }
                }
            }
        }
    }
}