package it.polito.lab5.gui.myProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.model.KPI
import it.polito.lab5.ui.theme.interFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileTopBar(
    onSignOut : () -> Unit,
    optionsOpened: Boolean,
    setOptionsOpenedValue: (Boolean) -> Unit,
    navController: NavController
) {
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
                text = "My Profile",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
        },
        actions = {
            OptionsComp(
                onSignOut = onSignOut,
                optionsOpened = optionsOpened,
                setOptionsOpenedValue = setOptionsOpenedValue,
                navController = navController
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
fun MyProfilePage(
    first: String,
    last: String,
    nickname: String,
    email: String,
    telephone: String,
    location: String,
    description: String,
    imageProfile: ImageProfile,
    joinedTeams: Long,
    kpi: Map<String, KPI>,
    paddingValues: PaddingValues
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val spacerModifier = Modifier.height(18.dp)
        val colors = MaterialTheme.colorScheme
        Spacer(modifier = Modifier.height(10.dp))
        OverlappingComponents(
            first = first,
            last = last,
            imageProfile = imageProfile,
            joinedTeams = joinedTeams,
            kpi = kpi
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextPresentationComp(
            text = first.plus(" ").plus(last),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            color = colors.onBackground
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        TextPresentationComp(
            text = description,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = colors.outline
        )

        Spacer(modifier = Modifier.height(32.dp))
        TextPresentationComp(
            text = nickname,
            label = "Nickname",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colors.onBackground,

            icon = {
                Icon(
                    painterResource(id = R.drawable.profile_bold),
                    contentDescription = "Nickname Icon",
                    tint = colors.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        )

        Spacer(modifier = spacerModifier)

        TextPresentationComp(
            text = email,
            label = "Email",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colors.onBackground,

            icon = {
                Icon(
                    painterResource(id = R.drawable.mail),
                    contentDescription = "Email Icon",
                    tint = colors.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        )

        Spacer(modifier = spacerModifier)

        TextPresentationComp(
            text = telephone,
            label = "Telephone",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colors.onBackground,

            icon = {
                Icon(
                    painterResource(id = R.drawable.telephone),
                    contentDescription = "Telephone Icon",
                    tint = colors.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        )

        Spacer(modifier = spacerModifier)

        TextPresentationComp(
            text = location,
            label = "Location",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = colors.onBackground,

            icon = {
                Icon(
                    painterResource(id = R.drawable.location),
                    contentDescription = "Email Icon",
                    tint = colors.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
        Spacer(modifier = spacerModifier)
    }
}