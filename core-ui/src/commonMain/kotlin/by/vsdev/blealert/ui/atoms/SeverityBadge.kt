package by.vsdev.blealert.ui.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.domain.AlertSeverity
import kmp_ble_alert.core_ui.generated.resources.Res
import kmp_ble_alert.core_ui.generated.resources.severity_critical
import kmp_ble_alert.core_ui.generated.resources.severity_info
import kmp_ble_alert.core_ui.generated.resources.severity_warning
import org.jetbrains.compose.resources.stringResource

@Composable
fun SeverityBadge(severity: AlertSeverity, modifier: Modifier = Modifier) {
    val color = when (severity) {
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
        AlertSeverity.WARNING -> MaterialTheme.colorScheme.primary
        AlertSeverity.INFO -> MaterialTheme.colorScheme.outline
    }
    val text = stringResource(
        when (severity) {
            AlertSeverity.CRITICAL -> Res.string.severity_critical
            AlertSeverity.WARNING -> Res.string.severity_warning
            AlertSeverity.INFO -> Res.string.severity_info
        },
    )
    Text(
        text = text,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color, RoundedCornerShape(percent = 50))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
