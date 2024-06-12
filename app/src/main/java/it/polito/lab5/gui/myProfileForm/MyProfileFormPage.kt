package it.polito.lab5.gui.myProfileForm

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import it.polito.lab5.LocalTheme
import it.polito.lab5.R
import it.polito.lab5.gui.ImagePresentationComp
import it.polito.lab5.gui.TextFieldComp
import it.polito.lab5.gui.teamForm.OptionsBottomSheet
import it.polito.lab5.model.ImageProfile
import it.polito.lab5.ui.theme.interFamily
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileFormTopBar(
    validate: suspend () -> Boolean,
    showLoading: Boolean,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val containerColor = if(LocalTheme.current.isDark) colors.surfaceColorAtElevation(10.dp) else colors.primary

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = colors.onBackground,
        ),
        title = {
        },
        navigationIcon = {
            TextButton(
                enabled = !showLoading,
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = colors.onBackground,
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                ),
                contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    contentDescription = "Back Icon"
                )

                Text(
                    text = "Back",
                    fontFamily = interFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp
                )
            }
        },
        actions = {
            if(showLoading) {
                CircularProgressIndicator(
                    color = colors.onBackground,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            else{
                TextButton(
                    onClick = {
                        scope.launch { if(validate()) navController.popBackStack() }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colors.onBackground
                    ),
                    contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
                ) {
                    Text(
                        text ="Save",
                        fontFamily = interFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    )
}

@Composable
fun MyProfileFormPage(
    first: String,
    setFirst: (String) -> Unit,
    firstError: (String),
    last: String,
    setLast: (String) -> Unit,
    lastError: (String),
    nickname: (String),
    setNickname: (String) -> Unit,
    nicknameError: (String),
    email: String,
    setEmail: (String) -> Unit,
    emailError: String,
    telephone: String,
    setTelephone: (String) -> Unit,
    telephoneError: String,
    location: String,
    setLocation: (String) -> Unit,
    locationError: String,
    description: String,
    setDescription: (String) -> Unit,
    descriptionError: String,
    imageProfile: ImageProfile,
    setImageValue: (ImageProfile) -> Unit,
    showBottomSheet: Boolean,
    setShowBottomSheetValue: (Boolean) -> Unit,
    cameraContract: ManagedActivityResultLauncher<Intent, ActivityResult>,
    galleryContract: ManagedActivityResultLauncher<String, Uri?>,
    paddingValues: PaddingValues
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 28.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val spacerModifier = Modifier.height(12.dp)
        val defaultOpt = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Text,
            capitalization = KeyboardCapitalization.Words,
            autoCorrect = false,
        )
        val nicknameOpt = defaultOpt.copy(capitalization = KeyboardCapitalization.None)
        val emailOpt = defaultOpt.copy(keyboardType = KeyboardType.Email, capitalization = KeyboardCapitalization.None)
        val telephoneOpt = defaultOpt.copy(keyboardType = KeyboardType.Phone, capitalization = KeyboardCapitalization.None)
        val locationOpt = defaultOpt.copy(autoCorrect = true)
        val descriptionOpt = defaultOpt.copy(imeAction = ImeAction.Done, capitalization = KeyboardCapitalization.Sentences, autoCorrect = true)
        val colors = MaterialTheme.colorScheme

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
                        tint = colors.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }

                ImagePresentationComp(
                    first = first,
                    last = last,
                    imageProfile = imageProfile,
                    fontSize = 60.sp
                )
            }
        }

        TextFieldComp(first, setFirst, firstError, "Firstname", 1, defaultOpt, 30) {
            Icon(painter = painterResource(id = R.drawable.profile_bold), contentDescription = "Person Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(last, setLast, lastError, "Lastname", 1, defaultOpt, 30) {
            Icon(painter = painterResource(id = R.drawable.profile_bold), contentDescription = "Person Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(nickname, setNickname, nicknameError, "Nickname", 1, nicknameOpt, 30) {
            Icon(painter = painterResource(id = R.drawable.profile_bold), contentDescription = "Person Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(email, setEmail, emailError, "Email", 1, emailOpt, 50) {
            Icon(painter = painterResource(id = R.drawable.mail), contentDescription = "Email Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(telephone, setTelephone, telephoneError, "Telephone", 1, telephoneOpt) {
            Icon(painter = painterResource(id = R.drawable.telephone), contentDescription = "Telephone Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(location, setLocation, locationError, "Location", 1, locationOpt, 60) {
            Icon(painter = painterResource(id = R.drawable.location), contentDescription = "Location Icon")
        }
        Spacer(spacerModifier)
        TextFieldComp(description, setDescription, descriptionError, "Description", 5, descriptionOpt, 250) {
            Icon(painter = painterResource(id = R.drawable.info_bold), contentDescription = "Description Icon")
        }
        Spacer(spacerModifier)

        if (showBottomSheet) {
            OptionsBottomSheet(
                image = imageProfile,
                setImageValue = setImageValue,
                setShowBottomSheetValue = setShowBottomSheetValue,
                cameraContract = cameraContract,
                galleryContract = galleryContract
            )
        }
    }
}