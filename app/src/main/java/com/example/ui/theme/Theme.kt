package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTealPrimary,
    secondary = MedicalTealAccent,
    tertiary = YellowWarning,
    background = MedicalDarkBackground,
    surface = MedicalDarkSurface,
    onPrimary = PureWhite,
    onSecondary = MedicalDarkBackground,
    onBackground = MedicalDarkOnSurface,
    onSurface = MedicalDarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTealPrimary,
    secondary = MedicalTealDark,
    tertiary = MedicalTealAccent,
    background = CreamWhite,
    surface = PureWhite,
    onPrimary = PureWhite,
    onSecondary = PureWhite,
    onBackground = SlateText,
    onSurface = SlateText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamicColor by default to enforce Aon Pharma branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
