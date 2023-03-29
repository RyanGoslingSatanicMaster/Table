package com.doggystyle.table.ui.theme

import android.app.Activity
import android.content.ContentResolver
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat


private val DarkColorPalette = darkColors(
    primary = Primary,
    primaryVariant = Primary,
    secondary = Secondary
)
// TODO override style of app for themes
private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = Primary,
    secondary = Secondary

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TableTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.statusBarColor = Color.Transparent.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

fun isGestureNavigationMode(content: ContentResolver?): Boolean {
    return Settings.Secure.getInt(content, "navigation_mode", 0) === 2
}

fun String.convertToTime(): Int{
    if (length > 1 && startsWith("0"))
        return replaceFirst("0", "").toInt()
    return this.toInt()
}

fun String.validateInputTimeMinute(): Boolean{
    return "^([0-5]?[0-9])".toRegex().matches(this)
}
fun String.validateInputTimeHours(): Boolean{
    return "^([0-1]?[0-9]|2[0-3])".toRegex().matches(this)

}
