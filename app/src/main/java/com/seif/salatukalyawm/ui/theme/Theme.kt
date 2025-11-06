// In: ui/theme/Theme.kt
package com.seif.salatukalyawm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.seif.salatukalyawm.data.SettingsManager

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* You can override more default colors if needed:
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun SalatukAlyawmTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // --- Theme Mode Logic from SettingsManager ---
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val themeMode by settingsManager.themeModeFlow.collectAsState(
        initial = SettingsManager.DEFAULT_THEME
    )

    val useDarkTheme = when (themeMode) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme() // "System"
    }

    // --- Dynamic Color Logic ---
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme)
                dynamicDarkColorScheme(context)
            else
                dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // --- Apply Material Theme ---
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
