package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoogleBluePrimaryDark,
    onPrimary = GoogleOnPrimaryDark,
    primaryContainer = GooglePrimaryContainerDark,
    onPrimaryContainer = GoogleOnPrimaryContainerDark,
    secondary = GoogleBluePrimaryDark,
    onSecondary = GoogleOnPrimaryDark,
    secondaryContainer = GoogleSurfaceVariantDark,
    onSecondaryContainer = GoogleOnSurfaceDark,
    tertiary = AssistantGreen,
    background = GoogleSurfaceDark,
    onBackground = GoogleOnSurfaceDark,
    surface = GoogleSurfaceDark,
    onSurface = GoogleOnSurfaceDark,
    surfaceVariant = GoogleSurfaceVariantDark,
    onSurfaceVariant = GoogleOnSurfaceVariantDark,
    surfaceContainer = GoogleSurfaceCardDark,
    surfaceContainerHigh = GoogleSurfaceVariantDark,
    outline = GoogleOutlineDark,
    outlineVariant = GoogleOutlineVariantDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = GoogleBluePrimary,
    onPrimary = GoogleOnPrimary,
    primaryContainer = GooglePrimaryContainer,
    onPrimaryContainer = GoogleOnPrimaryContainer,
    secondary = GoogleBluePrimary,
    onSecondary = GoogleOnPrimary,
    secondaryContainer = GoogleSurfaceVariant,
    onSecondaryContainer = GoogleOnSurface,
    tertiary = AssistantGreen,
    background = GoogleSurface,
    onBackground = GoogleOnSurface,
    surface = GoogleSurface,
    onSurface = GoogleOnSurface,
    surfaceVariant = GoogleSurfaceVariant,
    onSurfaceVariant = GoogleOnSurfaceVariant,
    surfaceContainer = GoogleSurfaceCard,
    surfaceContainerHigh = GoogleSurfaceVariant,
    outline = GoogleOutline,
    outlineVariant = GoogleOutlineVariant,
  )

@Composable
fun AssistantTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep Google Assistant authentic clean palette
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

