package it.polito.lab5.gui.myChats

import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun dateFormatter(messageLocalDateTime: LocalDateTime?): String? {
    if (messageLocalDateTime == null) { return null }
    val currentDate = LocalDate.now()
    val messageLocalDate = messageLocalDateTime.toLocalDate()

    if(messageLocalDateTime.isAfter(LocalDateTime.now()))
        Log.e("ERROR", "date is in the future")

    val literalDate = when (val daysDiff = ChronoUnit.DAYS.between(messageLocalDate, currentDate)) {
        0L -> messageLocalDateTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
        1L -> "Yesterday"
        in 2..3 -> "$daysDiff days ago"
        else -> messageLocalDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH))
    }

    return literalDate
}