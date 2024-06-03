package it.polito.lab5.gui.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily

@Composable
fun SignInPage(
    onSignInClick: () -> Unit,
    isSignedIn: Boolean,
    setIsSignedInValue: (Boolean) -> Unit){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {


        //image




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

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Welcome!",
            fontFamily = interFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp)

        Text(text = "Streamline tasks, track progress, and collaborate seamlessly with our intuitive team management app. \n" +
                "Boost productivity and stay organized effortlessly!",
            fontFamily = interFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp)

        Spacer(Modifier.height(32.dp))

        Button(
            shape= ButtonDefaults.textShape,
            onClick = {
                if (isSignedIn) onSignInClick()
                setIsSignedInValue(true)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = CollaborantColors.DarkBlue,
                contentColor = Color.White
            ),
            modifier = Modifier
                .padding(8.dp)
                .height(50.dp)
                .width(300.dp)
        ) {
            Text(
                text = if (isSignedIn) "Sign in" else "Get Started",
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp // Adjusted text size to fit inside the button
            )
        }
    }
}