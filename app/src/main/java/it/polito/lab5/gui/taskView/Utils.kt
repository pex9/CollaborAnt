package it.polito.lab5.gui.taskView

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getTimeAgo(date: LocalDateTime): String {
    val minute = 60
    val hour = minute * 60
    val day = hour * 24
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    val formattedDate = date.format(dateFormatter)
    val formattedTime = date.format(timeFormatter)
    val difference = Duration.between(date, LocalDateTime.now()).seconds

    if (difference < minute) return "Now"
    else if (difference < hour) {
        val minutes = difference / minute

        return if (minutes.toInt() == 1) { "$minutes minute ago" }
            else { "$minutes minutes ago" }
    } else if (difference < day) {
        val hours = difference / hour

        return if (hours.toInt() == 1) { "$hours hour ago" }
            else { "$hours hours ago" }
    } else {
        return formattedDate.plus(" at ").plus(formattedTime)
    }

}

fun openDocument(context: Context, documentUri: Uri) {
    // Open the document using an appropriate application
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(documentUri, context.contentResolver.getType(documentUri))
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.startActivity(intent)
}

fun getAttachmentInfo(context: Context, uri: Uri): Pair<String, Float>? {
    var fileName = ""
    var fileSize = 0f
    val projection = arrayOf(
        MediaStore.Images.Media.DATA,
        OpenableColumns.DISPLAY_NAME,
        OpenableColumns.SIZE
    )

    try {
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex)
                }

                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    fileSize = cursor.getLong(sizeIndex).toFloat()
                }
            }
        }
    } catch (e: Exception) {
        Log.e("Attachment error", e.message.toString())
    }

    return if(fileName.isNotBlank() && fileSize != 0f ) { Pair(fileName, fileSize) }
        else { null }
}

fun getLiteralSize(size: Float): String {
    var used = 0
    var s = size / 1024f
    val units = arrayOf("KB", "MB")

    while (s > 1024f && used < units.lastIndex) {
        s /= 1024f
        used++
    }

    return "%.2f %s".format(s, units[used])
}