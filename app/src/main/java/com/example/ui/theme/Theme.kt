package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightEducationalColorScheme = lightColorScheme(
  primary = PrimaryEmerald,
  onPrimary = Color.White,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = SecondaryTeal,
  onSecondary = Color.White,
  secondaryContainer = SecondaryContainer,
  onSecondaryContainer = OnSecondaryContainer,
  tertiary = TertiaryAmber,
  onTertiary = Color.White,
  tertiaryContainer = TertiaryContainer,
  onTertiaryContainer = OnTertiaryContainer,
  background = BackgroundLight,
  onBackground = TextPrimary,
  surface = SurfaceLight,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = OutlineLight,
  error = ErrorRed,
  onError = Color.White,
  errorContainer = ErrorContainer,
  onErrorContainer = Color(0xFF410002)
)

private val DarkEducationalColorScheme = darkColorScheme(
  primary = Color(0xFF81C784),
  onPrimary = Color(0xFF00390E),
  primaryContainer = Color(0xFF1B5E20),
  onPrimaryContainer = Color(0xFFA5D6A7),
  secondary = Color(0xFF80CBC4),
  onSecondary = Color(0xFF003730),
  secondaryContainer = Color(0xFF004D40),
  onSecondaryContainer = Color(0xFFB2DFDB),
  tertiary = Color(0xFFFFB74D),
  onTertiary = Color(0xFF451A03),
  tertiaryContainer = Color(0xFF78350F),
  onTertiaryContainer = Color(0xFFFDE68A),
  background = Color(0xFF121512),
  onBackground = Color(0xFFE2E3DE),
  surface = Color(0xFF1A1D1A),
  onSurface = Color(0xFFE2E3DE),
  surfaceVariant = Color(0xFF282F28),
  onSurfaceVariant = Color(0xFFC3C8C2),
  outline = Color(0xFF424A42),
  error = Color(0xFFFFB4AB),
  onError = Color(0xFF690005)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Preserve educational green/white branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkEducationalColorScheme else LightEducationalColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
