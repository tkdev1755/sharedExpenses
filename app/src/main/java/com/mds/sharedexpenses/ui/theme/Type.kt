package com.mds.sharedexpenses.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mds.sharedexpenses.R

//TODO: add correct files once they are added
val clashgrotesk = FontFamily(
    Font(R.font.clashgrotesk_extralight, FontWeight.ExtraLight),
    Font(R.font.clashgrotesk_light, FontWeight.Light),
    Font(R.font.clashgrotesk_medium, FontWeight.Medium),
    Font(R.font.clashgrotesk_bold, FontWeight.Bold),
    Font(R.font.clashgrotesk_semibold, FontWeight.SemiBold)
)
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = clashgrotesk),
    displayMedium = baseline.displayMedium.copy(fontFamily = clashgrotesk),
    displaySmall = baseline.displaySmall.copy(fontFamily = clashgrotesk),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = clashgrotesk),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = clashgrotesk),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = clashgrotesk),
    titleLarge = baseline.titleLarge.copy(fontFamily = clashgrotesk),
    titleMedium = baseline.titleMedium.copy(fontFamily = clashgrotesk),
    titleSmall = baseline.titleSmall.copy(fontFamily = clashgrotesk),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = clashgrotesk),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = clashgrotesk),
    bodySmall = baseline.bodySmall.copy(fontFamily = clashgrotesk),
    labelLarge = baseline.labelLarge.copy(fontFamily = clashgrotesk),
    labelMedium = baseline.labelMedium.copy(fontFamily = clashgrotesk),
    labelSmall = baseline.labelSmall.copy(fontFamily = clashgrotesk),
)
