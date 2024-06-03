package it.polito.lab5.gui.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import it.polito.lab5.R

fun getFlashIconId(flashMode: Int): Int {
    return when(flashMode) {
        ImageCapture.FLASH_MODE_AUTO -> R.drawable.baseline_flash_auto_24
        ImageCapture.FLASH_MODE_ON -> R.drawable.baseline_flash_on_24
        else -> R.drawable.baseline_flash_off_24
    }
}

fun takePhoto(context: Context, controller: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)

                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }
    )
}