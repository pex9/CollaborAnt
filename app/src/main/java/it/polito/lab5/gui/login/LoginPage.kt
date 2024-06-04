package it.polito.lab5.gui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.R
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
fun LoginPage(onSignInClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()

            //.padding(top= 60.dp,start=32.dp,end=32.dp,bottom=16.dp),
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground), // Replace with your image name
            contentDescription = "Description of your image",
            //contentScale = ContentScale.Fit,
            //modifier = Modifier.clip(RoundedCornerShape(15.dp)), // Adjust the size as needed, // Adjust the modifier as needed
            //contentScale = ContentScale.Crop // Adjust content scaling as needed
        )

        Text(
            text = "CollaborAnt", // App title
            maxLines = 1,
            fontFamily = interFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        CollaborantColors.DarkBlue,
                        CollaborantColors.Yellow
                    ) // Gradient colors
                )
            )
        )

        Spacer(Modifier.height(100.dp))

        Text(
            text = "Welcome!",
            fontFamily = interFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp)

        Text(text = "Streamline tasks, track progress, and collaborate seamlessly with our intuitive team management app. \n" +
                "Boost productivity and stay organized effortlessly!",
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center)

        Spacer(Modifier.height(80.dp))

        Button(
            shape = RoundedCornerShape(16.dp),
            onClick = {onSignInClick() },
            colors = ButtonDefaults.buttonColors(
                containerColor = CollaborantColors.DarkBlue,
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(8.dp)
                .height(50.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "LOGIN WITH GOOGLE",
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp // Adjusted text size to fit inside the button
            )
        }
    }
}