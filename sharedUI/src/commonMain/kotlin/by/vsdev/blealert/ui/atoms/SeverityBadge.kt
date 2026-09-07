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
import by.vsdev.blealert.core.alert.AlertSeverity

@Composable
fun SeverityBadge(severity: AlertSeverity, modifier: Modifier = Modifier) {
    val color = when (severity) {
        AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
        AlertSeverity.WARNING -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.INFO -> MaterialTheme.colorScheme.outline
    }
    Text(
        text = severity.name,
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(color, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
