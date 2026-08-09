package co.soporteti.mesati.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    primary = Purple,
    secondary = Green,
    background = PurpleLight,
    surface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun MesaTITheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AppColors, content = content)
}
