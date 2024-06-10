package it.polito.lab5.gui.teamForm

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.lab5.R
import it.polito.lab5.gui.camera.CameraActivity
import it.polito.lab5.model.Empty
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import it.polito.lab5.viewModels.pickRandomColor
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OptionsBottomSheet(
    image: ImageProfile,
    setImageValue: (ImageProfile) -> Unit,
    setShowBottomSheetValue: (Boolean) -> Unit, // Callback to toggle the visibility of the bottom sheet
    cameraContract: ManagedActivityResultLauncher<Intent, ActivityResult>,
    galleryContract: ManagedActivityResultLauncher<String, Uri?>
) {
    // Remember the state of the modal bottom sheet
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Remember coroutine scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { setShowBottomSheetValue(false) }, // Dismiss the bottom sheet when requested
        containerColor = colors.surfaceColorAtElevation(10.dp), // Background color of the bottom sheet
        dragHandle = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                BottomSheetDefaults.DragHandle()
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Modify team image",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.Center),
                        color = colors.onBackground
                    )

                    IconButton(
                        onClick = {
                            coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                                setShowBottomSheetValue(false)
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.cross),
                            contentDescription = "Close Icon",
                            tint = colors.onBackground
                            )

                    }
                }

            }
        },
        modifier = Modifier.fillMaxWidth() // Fill maximum width
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = colors.surface,
                contentColor = colors.onBackground
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            border = BorderStroke(1.dp, colors.outline.copy(0.4f))
        ) {
            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = "Take a photo",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = colors.onBackground
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "Camera Icon",
                        modifier = Modifier.size(28.dp)
                    )
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        setShowBottomSheetValue(false)
                    }
                    cameraContract.launch(Intent(context, CameraActivity::class.java))
                },
                colors = ListItemDefaults.colors(containerColor = colors.surface)
            )

            Divider(
                thickness = 1.dp,
                color = colors.outline,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            ListItem(
                headlineContent = {},
                leadingContent = {
                    Text(
                        text = "Upload a photo",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = colors.onBackground
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.gallery),
                        contentDescription = "Gallery Icon",
                        modifier = Modifier.size(28.dp)
                    )
                },
                modifier = Modifier.clickable {
                    coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        setShowBottomSheetValue(false)
                    }
                    galleryContract.launch("image/*")
                },
                colors = ListItemDefaults.colors(containerColor = colors.surface)
            )

            if(image !is Empty) {
                Divider(
                    thickness = 1.dp,
                    color = colors.outline.copy(0.4f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                ListItem(
                    headlineContent = {},
                    leadingContent = {
                        Text(
                            text = "Remove photo",
                            fontFamily = interFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = colors.error,
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "Delete Icon",
                            tint = colors.error,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    modifier = Modifier.clickable {
                        coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            setShowBottomSheetValue(false)
                            setImageValue(Empty(pickRandomColor()))
                        }
                    },
                    colors = ListItemDefaults.colors(containerColor = colors.surface)
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}
