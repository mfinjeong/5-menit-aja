package id.menitaja.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Coffee,
    onPrimary = Surface,
    primaryContainer = CoffeeDark,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceWarm,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    secondary = Sage,
    secondaryContainer = SageLight,
)

@Composable
fun MenitAjaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content,
    )
}
