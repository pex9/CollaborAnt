package it.polito.lab5.gui.teamForm

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.ui.theme.CollaborantColors
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TeamFormTopBar(validate: suspend () -> String, navController: NavController, team: String?) {
    val scope = rememberCoroutineScope()

    CenterAlignedTopAppBar(
        // Set custom colors for the top app bar
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            Text(
                text = if (team == null) "New Team" else "Edit Team",
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
        },
        navigationIcon = {
            // Navigation button to navigate back
            TextButton(
                onClick = { navController.popBackStack() }, // Navigate back when clicked
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background
                    contentColor = CollaborantColors.DarkBlue // Dark blue icon color
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
                    fontSize = 20.sp // Font size
                )
            }
        },
        actions = {
            TextButton(
                onClick = {
                    scope.launch {
                        val teamId = validate()
                        if(teamId.isNotBlank()) {
                            navController.popBackStack()
                            if(team == null) {
                                navController.navigate("infoTeam/${teamId}")
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = CollaborantColors.DarkBlue
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Text(
                    text = if (team == null) "Create" else "Save",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        }
    )
}

@Composable
fun TeamFormPage(
    name: String,
    setNameValue: (String) -> Unit,
    nameError: String,
    description: String,
    setDescriptionValue: (String) -> Unit,
    descriptionError: String,
    image: ImageProfile,
    setImageValue: (ImageProfile) -> Unit,
    showBottomSheet: Boolean,
    setShowBottomSheetValue: (Boolean) -> Unit,
    cameraContract: ManagedActivityResultLauncher<Intent, ActivityResult>,
    galleryContract: ManagedActivityResultLauncher<String, Uri?>,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val (first, last) = getMonogramText(name)
        val defaultOpt = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
        )
        val descriptionOpt = defaultOpt.copy(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = true
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .padding(18.dp)
            ) {
                IconButton(
                    onClick = { setShowBottomSheetValue(true) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 24.dp, y = (-12).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit_square),
                        contentDescription = "Edit Icon",
                        tint = CollaborantColors.DarkBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }

                ImagePresentationComp(
                    first = first,
                    last = last,
                    imageProfile = image,
                    fontSize = 60.sp
                )
            }
        }

        TextFieldComp(
            value = name,
            updateValue = setNameValue,
            errorMsg = nameError,
            label = "Team name",
            numLines = 1,
            options = defaultOpt,
            maxChars = 50
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextFieldComp(
            value = description,
            updateValue = setDescriptionValue,
            errorMsg = descriptionError,
            label = "Description",
            numLines = 5,
            options = descriptionOpt,
            maxChars = 250
        )
    }

    if (showBottomSheet) {
        OptionsBottomSheet(
            image = image,
            setImageValue = setImageValue,
            setShowBottomSheetValue = setShowBottomSheetValue,
            cameraContract = cameraContract,
            galleryContract = galleryContract
        )
    }
}