package it.polito.lab5.gui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.ui.theme.interFamily

@Composable
fun LoginPage(showLoading: Boolean, onSignInClick: () -> Unit, isHorizontal: Boolean) {
    val colors = MaterialTheme.colorScheme
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

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val descriptionText = "Streamline tasks, track progress, " +
                "and collaborate seamlessly with our intuitive team management app. \n" +
                "Boost productivity and stay organized effortlessly!"
        val layoutModifier1 = if(!isHorizontal) Modifier.weight(1.6f) else Modifier
        val layoutModifier2 = if(!isHorizontal) Modifier.weight(2f) else Modifier
        val layoutModifier3 = if(!isHorizontal) Modifier.weight(1.6f).padding(top = 60.dp) else Modifier
        val imgModifier = if(!isHorizontal) Modifier.size(320.dp) else Modifier.size(320.dp)
        if(!isHorizontal) Spacer(modifier = Modifier.height(35.dp))
        Column(
            modifier = layoutModifier1.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.mipmap.logo_foreground), // Replace with your image name
                contentDescription = "CollaborAnt logo image",
                modifier = imgModifier
            )
        }

        Column(
            modifier = layoutModifier2.fillMaxWidth().padding(bottom = 30.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome in CollaborAnt", // App title
                maxLines = 1,
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = gradientColors// Gradient colors
                    )
                ),
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = descriptionText,
                fontFamily = interFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = colors.onBackground,
                lineHeight = 28.sp,
                modifier = Modifier.padding(horizontal = 5.dp)
            )

        }

        if(isHorizontal) Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = layoutModifier3.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = { onSignInClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.secondaryContainer,
                    contentColor = colors.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(8.dp),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                if (showLoading) {
                    CircularProgressIndicator(color = colors.onBackground)
                } else {
                    Text(
                        text = "LOGIN WITH GOOGLE",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp,
                        color = colors.background
                    )
                }
            }
        }

        if(isHorizontal) Spacer(modifier = Modifier.height(40.dp))
    }
}