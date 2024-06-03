package it.polito.lab5.viewModels

import androidx.compose.ui.graphics.Color
import it.polito.lab5.ui.theme.CollaborantColors

fun pickRandomColor(): Color {
    val colorPool = listOf(
        CollaborantColors.LightBlue,
        CollaborantColors.MediumBlue,
        CollaborantColors.DarkBlue,
        CollaborantColors.CardBackGroundGray,
        CollaborantColors.Yellow
    )
    return colorPool.random()
}