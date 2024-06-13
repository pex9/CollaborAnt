package it.polito.lab5.viewModels

import androidx.compose.ui.graphics.Color
import it.polito.lab5.ui.theme.CollaborantColors

fun pickRandomColor(): Color {
    val colorPool = listOf(
        CollaborantColors.Primary,
        CollaborantColors.Secondary,
        CollaborantColors.PrimaryVariant,
        CollaborantColors.Outline,
        CollaborantColors.Primary.copy(alpha = 0.4f),
        CollaborantColors.Secondary.copy(alpha = 0.4f),
        CollaborantColors.PrimaryVariant.copy(alpha = 0.4f),
        CollaborantColors.Outline.copy(alpha = 0.4f),
    )
    return colorPool.random()
}