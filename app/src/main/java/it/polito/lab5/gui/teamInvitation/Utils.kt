package it.polito.lab5.gui.teamInvitation

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

fun generateQRCode(text: String): Bitmap? {
    return try {
        val barcodeEncoder = BarcodeEncoder()
        val bitMatrix: BitMatrix = barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 600, 600)
        barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}