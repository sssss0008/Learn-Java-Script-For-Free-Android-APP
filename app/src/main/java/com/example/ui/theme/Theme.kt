package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
  SYSTEM, LIGHT, DARK, AMOLED, HIGH_CONTRAST
}

private val DarkColorScheme = darkColorScheme(
  primary = ElectricBlue,
  onPrimary = NavyDeep,
  primaryContainer = NavySurfaceVariant,
  onPrimaryContainer = ElectricBlue,
  secondary = JsYellow,
  onSecondary = NavyDeep,
  secondaryContainer = NavySurfaceVariant,
  onSecondaryContainer = JsYellow,
  tertiary = NeonGreen,
  onTertiary = NavyDeep,
  background = NavyBackground,
  onBackground = Color(0xFFF1F5F9),
  surface = NavySurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = NavySurfaceVariant,
  onSurfaceVariant = Color(0xFFCBD5E1),
  outline = NavyBorder,
  outlineVariant = Color(0xFF1E2A4A),
  error = CoralRed,
  onError = Color.White
)

private val LightColorScheme = lightColorScheme(
  primary = ElectricBlueDark,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = JsYellowDark,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFFFEF3C7),
  onSecondaryContainer = Color(0xFF92400E),
  tertiary = NeonGreen,
  onTertiary = Color.White,
  background = LightBg,
  onBackground = LightTextPrimary,
  surface = LightSurface,
  onSurface = LightTextPrimary,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = LightTextSecondary,
  outline = LightBorder,
  outlineVariant = Color(0xFFE2E8F0),
  error = CoralRed,
  onError = Color.White
)

private val AmoledColorScheme = darkColorScheme(
  primary = ElectricBlue,
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF111827),
  onPrimaryContainer = ElectricBlue,
  secondary = JsYellow,
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF1F2937),
  onSecondaryContainer = JsYellow,
  tertiary = NeonGreen,
  onTertiary = Color.Black,
  background = AmoledBg,
  onBackground = Color(0xFFF8FAFC),
  surface = AmoledSurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = Color(0xFF111827),
  onSurfaceVariant = Color(0xFF94A3B8),
  outline = AmoledBorder,
  outlineVariant = Color(0xFF262626),
  error = CoralRed,
  onError = Color.Black
)

private val HighContrastColorScheme = darkColorScheme(
  primary = Color(0xFF38BDF8),
  onPrimary = Color.Black,
  primaryContainer = Color(0xFF00365A),
  onPrimaryContainer = Color.White,
  secondary = Color(0xFFFFF066),
  onSecondary = Color.Black,
  secondaryContainer = Color(0xFF4A4000),
  onSecondaryContainer = Color.White,
  tertiary = Color(0xFF00FF88),
  onTertiary = Color.Black,
  background = Color.Black,
  onBackground = Color.White,
  surface = Color(0xFF0D0D0D),
  onSurface = Color.White,
  surfaceVariant = Color(0xFF1A1A1A),
  onSurfaceVariant = Color.White,
  outline = Color.White,
  outlineVariant = Color(0xFF888888),
  error = Color(0xFFFF4D4D),
  onError = Color.Black
)

@Composable
fun LearnJsTheme(
  themeMode: AppThemeMode = AppThemeMode.DARK,
  content: @Composable () -> Unit
) {
  val systemDark = isSystemInDarkTheme()
  val colorScheme = when (themeMode) {
    AppThemeMode.SYSTEM -> if (systemDark) DarkColorScheme else LightColorScheme
    AppThemeMode.LIGHT -> LightColorScheme
    AppThemeMode.DARK -> DarkColorScheme
    AppThemeMode.AMOLED -> AmoledColorScheme
    AppThemeMode.HIGH_CONTRAST -> HighContrastColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
