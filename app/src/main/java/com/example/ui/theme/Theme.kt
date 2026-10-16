package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RepQuestColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = NeonVioletGlow,
    secondary = ElectricCyan,
    onSecondary = TextOnAccent,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = ElectricCyanGlow,
    tertiary = QuestGold,
    onTertiary = TextOnAccent,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = VibrantCoral,
    onError = Color.White
)

@Composable
fun RepQuestTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RepQuestColorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias if needed
@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    RepQuestTheme(content = content)
}
