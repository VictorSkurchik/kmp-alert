package by.vsdev.blealert.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Brand palette sampled from ryadom.ai: warm orange for calls to action and
// things that need attention, navy for headings, green for "all is well".
private val Orange = Color(0xFFFF7A1C)
private val OrangeContainer = Color(0xFFFFE0D1)
private val OnOrangeContainer = Color(0xFF7A3300)
private val Navy = Color(0xFF333961)
private val NavyContainer = Color(0xFFE6E8F2)
private val Green = Color(0xFF21CE6B)
private val GreenContainer = Color(0xFFECF9F0)
private val OnGreenContainer = Color(0xFF0F6B36)
private val Red = Color(0xFFE0483A)
private val RedContainer = Color(0xFFFCE4E1)
private val OnRedContainer = Color(0xFF7A241C)
private val Background = Color(0xFFFFFBF7)
private val Surface = Color(0xFFFFFFFF)
private val SurfaceVariant = Color(0xFFF4F1EC)
private val OnSurfaceVariant = Color(0xFF63625C)
private val Outline = Color(0xFFCBC6BE)
private val OnSurface = Color(0xFF2B2E44)

private val RyadomColorScheme = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OnOrangeContainer,
    secondary = Navy,
    onSecondary = Color.White,
    secondaryContainer = NavyContainer,
    onSecondaryContainer = Navy,
    tertiary = Green,
    onTertiary = Color.White,
    tertiaryContainer = GreenContainer,
    onTertiaryContainer = OnGreenContainer,
    error = Red,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = OnRedContainer,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
)

private val RyadomShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun BleAlertTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RyadomColorScheme,
        shapes = RyadomShapes,
        content = content,
    )
}
