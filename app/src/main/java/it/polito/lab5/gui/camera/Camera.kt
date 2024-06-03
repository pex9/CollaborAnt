package it.polito.lab5.gui.camera

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import it.polito.lab5.R
import it.polito.lab5.viewModels.CameraViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun CameraPreviewPane(cameraSelector: CameraSelector, updateCameraSelector: (CameraSelector) -> Unit,
                      flashMode: Int, updateFlashMode: (Int) -> Unit, onPhotoTaken: (Bitmap) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            setCameraSelector(cameraSelector)
            setImageCaptureFlashMode(flashMode)
            setTapToFocusEnabled(true)
            setPinchToZoomEnabled(true)
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        //  Back button
        IconButton(
            onClick = { (context as? Activity)?.finish() },
            modifier = if(this.maxHeight < this.maxWidth) Modifier.align(Alignment.TopStart).padding(16.dp)
                    else Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cancel Icon",
                tint = Color.White,
                modifier = Modifier.size(32.dp))
        }

        // Switch Camera button
        IconButton(
            onClick = {
                val newCameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                 else
                    CameraSelector.DEFAULT_BACK_CAMERA

                updateCameraSelector(newCameraSelector)
                controller.cameraSelector = newCameraSelector
            },
            modifier = if(this.maxHeight < this.maxWidth) Modifier.align(Alignment.TopEnd).size(32.dp).offset(x = (-51).dp, y = 48.dp)
                        else Modifier.align(Alignment.BottomEnd).size(32.dp).offset(x = (-48).dp, y = (-51).dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_cameraswitch_24),
                contentDescription = "Switch Camera Icon",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Capture image button
        IconButton(
            onClick = { takePhoto(context, controller, onPhotoTaken) },
            modifier = if(this.maxHeight < this.maxWidth) Modifier.align(Alignment.CenterEnd).size(74.dp).offset(x = (-32).dp)
                        else Modifier.align(Alignment.BottomCenter).size(74.dp).offset(y = (-32).dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.baseline_camera_24),
                contentDescription = "Capture Image Icon",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }

        if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            // Flash button
            IconButton(
                onClick = {
                    val newFlashMode = when (flashMode) {
                        ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                        ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                        else -> ImageCapture.FLASH_MODE_AUTO
                    }

                    updateFlashMode(newFlashMode)
                    controller.imageCaptureFlashMode = newFlashMode
                },
                modifier = if(this.maxHeight < this.maxWidth) Modifier.align(Alignment.BottomEnd).size(32.dp).offset(x = (-51).dp, y = (-48).dp)
                            else Modifier.align(Alignment.BottomStart).size(32.dp).offset(x = 48.dp, y = (-51).dp)
            ) {
                Icon(painter = painterResource(id = getFlashIconId(flashMode)),
                    contentDescription = "Flash Icon",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ImagePreviewPane(image: Bitmap, cancelImage: () -> Unit) {
    val context = LocalContext.current
    val p = MaterialTheme.colorScheme.primary
    val op = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(text = "") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = p),
            navigationIcon = {
                IconButton(
                    onClick = { cancelImage() },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = op)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back Icon")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val intent = Intent()
                        val stream = ByteArrayOutputStream()
                        image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val byteArray = stream.toByteArray()

                        // Create a file in the cache directory
                        val outputFile = File(context.cacheDir, "imageProfile.jpg")
                        outputFile.outputStream().use { it.write(byteArray) }

                        intent.putExtra("filename", outputFile.absolutePath)
                        (context as? Activity)?.setResult(RESULT_OK, intent)
                        (context as? Activity)?.finish()
                    },
                    colors = IconButtonDefaults.iconButtonColors(contentColor = op)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save image")
                }
            }
        )

        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = "Image Preview",
            modifier = Modifier.fillMaxSize().background(Color.Black)
        )
    }
}

@Composable
fun CameraScreen(vm: CameraViewModel) {
    if(vm.image == null) {
        CameraPreviewPane(
            cameraSelector = vm.cameraSelector,
            updateCameraSelector = vm::setCameraSelectorValue,
            flashMode = vm.flashMode,
            updateFlashMode = vm::setFlashModeValue,
            onPhotoTaken = vm::setImageValue
        )
    } else {
        ImagePreviewPane(vm.image!!) { vm.setImageValue(null) }
    }
}