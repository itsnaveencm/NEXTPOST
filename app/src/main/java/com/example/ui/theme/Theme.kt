package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AlertPulseColorScheme =
  darkColorScheme(
    primary = SoftLavender,
    onPrimary = AmoledBlack,
    primaryContainer = LavenderBadgeContainer,
    onPrimaryContainer = SoftLavender,
    secondary = TitaniumSilver,
    onSecondary = AmoledBlack,
    secondaryContainer = LavenderBadgeContainer,
    onSecondaryContainer = LavenderMutedText,
    tertiary = UrgentRose,
    onTertiary = AmoledBlack,
    tertiaryContainer = UrgentRoseContainer,
    onTertiaryContainer = UrgentRose,
    background = AmoledBlack,
    onBackground = PrimaryText,
    surface = ElevatedCardSurface,
    onSurface = PrimaryText,
    surfaceVariant = ElevatedCardSurfaceAlt,
    onSurfaceVariant = SecondaryText,
    surfaceContainerLowest = AmoledBlack,
    surfaceContainerLow = ElevatedBarSurface,
    surfaceContainer = ElevatedCardSurface,
    surfaceContainerHigh = ElevatedCardSurfaceAlt,
    surfaceContainerHighest = LavenderBadgeContainer,
    outline = SubtleBorder,
    outlineVariant = SubtleBorderHighlight,
  )

@Composable
fun AlertPulseTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = AlertPulseColorScheme,
    typography = Typography,
    shapes = AlertPulseShapes,
    content = content,
  )
}
