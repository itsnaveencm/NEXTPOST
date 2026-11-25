package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val DotMatrixFontFamily = FontFamily(Font(R.font.dotgothic16, FontWeight.Normal))

val GeometricSansFontFamily = FontFamily(Font(R.font.space_grotesk, FontWeight.Normal))

val TabularMonoFontFamily = FontFamily(Font(R.font.jetbrains_mono, FontWeight.Normal))

/**
 * Nothing OS-inspired DotGothic16 display styles + JetBrains Mono tabular metadata styles.
 */
object AlertPulseTypography {
  val BrandHeaderDot =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 20.sp,
      lineHeight = 24.sp,
      letterSpacing = 2.4.sp,
    )

  val HeroCounterDot =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 24.sp,
      lineHeight = 28.sp,
      letterSpacing = 1.6.sp,
    )

  val BentoCounterDot =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 22.sp,
      lineHeight = 26.sp,
      letterSpacing = 1.4.sp,
    )

  val SectionHeaderMono =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 15.sp,
      lineHeight = 20.sp,
      letterSpacing = 1.8.sp,
    )

  val StatusSubtitleDot =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 11.sp,
      lineHeight = 15.sp,
      letterSpacing = 1.1.sp,
    )

  val CounterMono =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp,
      lineHeight = 16.sp,
      letterSpacing = 1.4.sp,
    )

  val BadgeMono =
    TextStyle(
      fontFamily = DotMatrixFontFamily,
      fontWeight = FontWeight.Normal,
      fontSize = 11.sp,
      lineHeight = 14.sp,
      letterSpacing = 1.2.sp,
    )

  val DateMono =
    TextStyle(
      fontFamily = TabularMonoFontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 16.sp,
      letterSpacing = 0.5.sp,
      fontFeatureSettings = "tnum",
    )

  val VacancyTabularMono =
    TextStyle(
      fontFamily = TabularMonoFontFamily,
      fontWeight = FontWeight.Medium,
      fontSize = 11.sp,
      lineHeight = 14.sp,
      letterSpacing = 0.3.sp,
      fontFeatureSettings = "tnum",
    )
}

val Typography =
  Typography(
    headlineSmall =
      TextStyle(
        fontFamily = DotMatrixFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 1.2.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp,
      ),
    titleSmall =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.1.sp,
      ),
    bodySmall =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = GeometricSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.15.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = TabularMonoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = DotMatrixFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.9.sp,
      ),
  )
