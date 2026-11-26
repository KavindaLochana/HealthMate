package com.example.healthmate.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.healthmate.R

// Set of Material typography styles to start with

@OptIn(ExperimentalTextApi::class)
val InterFontFamily = FontFamily(
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Normal.weight)
        )
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Medium,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Medium.weight)
        )
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.SemiBold.weight)
        )
    ),
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(
            FontVariation.weight(FontWeight.Bold.weight)
        )
    )
)

private val DefaultTypography = Typography()

val Typography = Typography(
    displayLarge = DefaultTypography.displayLarge.copy(fontFamily = InterFontFamily),
    displayMedium = DefaultTypography.displayMedium.copy(fontFamily = InterFontFamily),
    displaySmall = DefaultTypography.displaySmall.copy(fontFamily = InterFontFamily),
    headlineLarge = DefaultTypography.headlineLarge.copy(fontFamily = InterFontFamily),
    headlineMedium = DefaultTypography.headlineMedium.copy(fontFamily = InterFontFamily),
    headlineSmall = DefaultTypography.headlineSmall.copy(fontFamily = InterFontFamily),
    titleLarge = DefaultTypography.titleLarge.copy(fontFamily = InterFontFamily),
    titleMedium = DefaultTypography.titleMedium.copy(fontFamily = InterFontFamily),
    titleSmall = DefaultTypography.titleSmall.copy(fontFamily = InterFontFamily),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(fontFamily = InterFontFamily),
    bodySmall = DefaultTypography.bodySmall.copy(fontFamily = InterFontFamily),
    labelLarge = DefaultTypography.labelLarge.copy(fontFamily = InterFontFamily),
    labelMedium = DefaultTypography.labelMedium.copy(fontFamily = InterFontFamily),
    labelSmall = DefaultTypography.labelSmall.copy(fontFamily = InterFontFamily)
)

//val Typography = Typography(
//    bodyLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 16.sp,
//        lineHeight = 24.sp,
//        letterSpacing = 0.5.sp
//    )
//    /* Other default text styles to override
//    titleLarge = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Normal,
//        fontSize = 22.sp,
//        lineHeight = 28.sp,
//        letterSpacing = 0.sp
//    ),
//    labelSmall = TextStyle(
//        fontFamily = FontFamily.Default,
//        fontWeight = FontWeight.Medium,
//        fontSize = 11.sp,
//        lineHeight = 16.sp,
//        letterSpacing = 0.5.sp
//    )
//    */
//)