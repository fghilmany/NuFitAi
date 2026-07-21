package com.fghilmany.nufitai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NuFitColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = SurfaceWhite,
    primaryContainer = AccentGreen30,
    onPrimaryContainer = PrimaryDark,
    secondary = PrimaryGreen,
    onSecondary = SurfaceWhite,
    secondaryContainer = AccentLightGreen,
    onSecondaryContainer = PrimaryGreen,
    tertiary = PrimaryLight,
    onTertiary = SurfaceWhite,
    background = SurfaceWhite,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextDark,
    outline = ButtonBorder,
    outlineVariant = SurfaceGray,
)

/**
 * NuFit AI theme matching the Figma design system.
 *
 * @param content The composable content to theme.
 */
@Composable
fun NuFitAITheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NuFitColorScheme,
        typography = NuFitTypography,
        content = content,
    )
}
