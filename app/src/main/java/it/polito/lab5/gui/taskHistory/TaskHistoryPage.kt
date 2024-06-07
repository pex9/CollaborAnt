package it.polito.lab5.gui.taskHistory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.model.Action
import it.polito.lab5.model.User
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HistoryTopBar(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colors.onBackground,
        ),
        title = {
            Text(
                text = "History",
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        },
        navigationIcon = {
            TextButton(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon",
                )

                Text(
                    text = "Back",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                )
            }
        }
    )
}

@Composable
fun TaskHistoryPage(history: List<Action>, users: List<User>, paddingValues: PaddingValues) {
    val colors = MaterialTheme.colorScheme
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(top=10.dp)
            .background(colors.background)
    ) {
        itemsIndexed(history) { index, action ->
            // Display each history row
            HistoryRow(
                action = action,
                users = users,
                isFirst = index == 0,
                isLast = index == history.lastIndex
            )
        }
    }
}
