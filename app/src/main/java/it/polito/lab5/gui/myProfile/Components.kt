package it.polito.lab5.gui.myProfile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.KPI
import it.polito.lab5.ui.theme.interFamily

@Composable
fun TextPresentationComp(
    text: String,
    label: String? = null,
    fontWeight: FontWeight,
    fontSize: TextUnit,
    color: Color = Color.Black,
    icon: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(2f)
        ) {
            icon?.let { icon ->
                icon()
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = text,
                fontFamily = interFamily,
                fontWeight = fontWeight,
                fontSize = fontSize,
                color = color
            )
        }

        if(label != null) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = colors.outline
                )
            }
        }
    }
}

@Composable
fun OverlappingComponents(
    first: String,
    last: String,
    imageProfile: ImageProfile,
    joinedTeams: Int,
    kpi: List<Pair<Int, KPI>>
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(292.dp)
        .padding(bottom = 18.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .align(Alignment.BottomCenter)
        ) {
            KPIPresentationComp(kpi, joinedTeams)
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(200.dp)
                .padding(18.dp)
                .align(Alignment.TopCenter)
        )  {
            ImagePresentationComp(
                first = first,
                last = last,
                imageProfile = imageProfile,
                fontSize = 60.sp
            )
        }
    }
}

@Composable
fun KPIPresentationComp(kpi: List<Pair<Int, KPI>>, joinedTeams: Int) {
    val colors = MaterialTheme.colorScheme
    val c = colors.outline.copy(0.4f)
    val overallUserKPI = mapOf(
        "assigned" to kpi.sumOf{ it.second.assignedTasks },
        "completed" to kpi.sumOf{ it.second.completedTasks }
    )

    Card(
        border = BorderStroke(1.dp, colors.outline),
        shape = CardDefaults.outlinedShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = joinedTeams.toString(),
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colors.onBackground
                )

                Text(
                    text = "Joined\nTeams",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colors.outline,
                    textAlign = TextAlign.Center
                )
            }

            Divider(modifier = Modifier.width(1.dp).height(40.dp), color = c)

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${overallUserKPI["assigned"]}",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colors.onBackground

                )

                Text(
                    text = "Assigned\nTasks",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colors.outline,
                    textAlign = TextAlign.Center
                )
            }

            Divider(modifier = Modifier.width(1.dp).height(40.dp), color = c)

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${overallUserKPI["completed"]}",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = colors.onBackground

                )

                Text(
                    text = "Completed\nTasks",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = colors.outline,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}