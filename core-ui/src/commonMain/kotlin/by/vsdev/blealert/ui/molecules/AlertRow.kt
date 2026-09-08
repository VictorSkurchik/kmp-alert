package by.vsdev.blealert.ui.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertSeverity
import by.vsdev.blealert.domain.AlertType
import by.vsdev.blealert.ui.atoms.SeverityBadge
import by.vsdev.blealert.ui.format.displayName
import by.vsdev.blealert.ui.format.formatRelativeTime

@Composable
fun AlertRow(alert: Alert, modifier: Modifier = Modifier) {
    val accentColor = alert.severity.accentColor()
    val accentContainer = alert.severity.accentContainerColor()

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(accentContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(imageVector = alert.type.icon(), contentDescription = null, tint = accentColor)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = alert.type.displayName(), style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = formatRelativeTime(alert.receivedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SeverityBadge(alert.severity)
        }
    }
}

@Composable
private fun AlertSeverity.accentColor(): Color = when (this) {
    AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.error
    AlertSeverity.WARNING -> MaterialTheme.colorScheme.primary
    AlertSeverity.INFO -> MaterialTheme.colorScheme.outline
}

@Composable
private fun AlertSeverity.accentContainerColor(): Color = when (this) {
    AlertSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
    AlertSeverity.WARNING -> MaterialTheme.colorScheme.primaryContainer
    AlertSeverity.INFO -> MaterialTheme.colorScheme.surfaceVariant
}

private fun AlertType.icon(): ImageVector = when (this) {
    AlertType.MOTION -> Icons.AutoMirrored.Filled.DirectionsWalk
    AlertType.SOS -> Icons.Filled.Sos
    AlertType.DEVICE_OFFLINE -> Icons.Filled.WifiOff
    AlertType.WOKE_UP -> Icons.Filled.WbSunny
    AlertType.LONG_BATHROOM_TIME -> Icons.Filled.Bathtub
    AlertType.NOT_RETURNED_HOME -> Icons.Filled.Home
    AlertType.NO_ACTIVITY -> Icons.Filled.HourglassEmpty
}
