package it.polito.lab5.viewModels

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CameraViewModel: ViewModel() {
    var image: Bitmap? by mutableStateOf(null)
        private set
    fun setImageValue (i: Bitmap?) {
        image = i
    }

    var cameraSelector by mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
        private set
    fun setCameraSelectorValue(c: CameraSelector) {
        cameraSelector = c
    }

    var flashMode by mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO)
        private set
    fun setFlashModeValue(f: Int) {
        flashMode = f
    }
}
