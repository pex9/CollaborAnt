package it.polito.lab5.gui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import it.polito.lab5.ui.theme.Lab4Theme
import it.polito.lab5.viewModels.CameraViewModel

class CameraActivity : ComponentActivity() {
    private val cameraViewModel: CameraViewModel by viewModels()
    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setCameraPreview()
            } else {
                // Camera permission denied
            }
        }

    private fun setCameraPreview() {
        setContent {
            Lab4Theme(darkTheme = false) {
                CameraScreen(cameraViewModel)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> {
                setCameraPreview()

                // Restore the state of the camera selector if it exists in the saved state bundle
                savedInstanceState?.let { bundle ->
                    val cameraSelectorValue = bundle.getInt("cameraSelector", 0)  // Default value is 0 if not found
                    val cameraSelector = if (cameraSelectorValue == 1) CameraSelector.DEFAULT_BACK_CAMERA
                        else CameraSelector.DEFAULT_FRONT_CAMERA

                    cameraViewModel.setCameraSelectorValue(cameraSelector)
                }
            }
            else -> cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the value of the camera selector in the saved state bundle
        val cameraSelectorValue = if (cameraViewModel.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) 1 else 2
        outState.putInt("cameraSelector", cameraSelectorValue)
    }
}