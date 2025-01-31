package it.polito.lab5.gui.teamInvitation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.teamForm.getMonogramText
import it.polito.lab5.model.Team
import it.polito.lab5.ui.theme.interFamily

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TeamInvitationTopBar(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary

    CenterAlignedTopAppBar(
        // Set custom colors for the top app bar
        colors = TopAppBarDefaults.topAppBarColors(containerColor = containerColor),
        title = {
            Text(
                text = "Invite",
                fontFamily = interFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = colors.onBackground
            )
        },
        navigationIcon = {
            // Navigation button to navigate back
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background
                    contentColor = colors.onBackground // Dark blue icon color
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding // Standard padding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left), // Back arrow icon
                    contentDescription = "Back Icon" // Description for accessibility
                )

                Text(
                    text = "Back", // Text displayed next to the back arrow
                    style = MaterialTheme.typography.titleLarge, // Text style
                    fontFamily = interFamily, // Custom font family
                    fontWeight = FontWeight.SemiBold, // Semi-bold font weight
                    fontSize = 20.sp, // Font size
                    color = colors.onBackground
                )
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TeamInvitationPage(team: Team, paddingValues: PaddingValues) {
    val context = LocalContext.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val url = "https://team.collaborant.com/${team.id}"
    val qrCodeBitmap = generateQRCode(url)
    val colors = MaterialTheme.colorScheme

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        Card(
            onClick = {
                clipboardManager.setText(AnnotatedString((url)))
                Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
            },
            colors = CardDefaults.cardColors(
                containerColor = colors.surfaceColorAtElevation(10.dp),
                contentColor = colors.onBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, colors.outline),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .padding(7.dp)
                ) {
                    val (first, last) = getMonogramText(team.name)

                    ImagePresentationComp(
                        first = first,
                        last = last,
                        imageProfile = team.image,
                        fontSize = 19.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = team.name,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = interFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = url,
                        fontWeight = FontWeight.Light,
                        fontSize = 14.sp,
                        fontFamily = interFamily,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        qrCodeBitmap?.let { bitmap ->
            Box(
                modifier = Modifier
                    .fillMaxSize() // Ensures the Box takes up the entire available space
                    .border(BorderStroke(1.dp, colors.outline), RoundedCornerShape(10.dp))
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    contentScale = ContentScale.Crop, // Ensures the image scales correctly
                    modifier = Modifier
                        .fillMaxSize() // Ensures the Image takes up the entire space of the Box
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colors.surfaceColorAtElevation(10.dp),
                contentColor = colors.onBackground
            ),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, colors.outline),
        ) {
            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = "Share link",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = interFamily,
                        color = colors.onBackground
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.upload),
                        contentDescription = "Share Icon",
                        tint = colors.onBackground
                    )
                },
                colors = ListItemDefaults.colors(containerColor = colors.surfaceColorAtElevation(10.dp)),
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, url)
                    context.startActivity(Intent.createChooser(intent, "Share Link"))
                }
            )

            Divider(
                thickness = 1.dp,
                color = colors.outline.copy(0.4f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = "Copy link",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        fontFamily = interFamily,
                        color = colors.onBackground
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.copy),
                        contentDescription = "Copy Icon",
                        tint = colors.onBackground
                    )
                },
                colors = ListItemDefaults.colors(containerColor = colors.surfaceColorAtElevation(10.dp)),
                modifier = Modifier.clickable {
                    clipboardManager.setText(AnnotatedString((url)))
                    Toast.makeText(context, "Link copied", Toast.LENGTH_SHORT).show()
                }
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
    }
}