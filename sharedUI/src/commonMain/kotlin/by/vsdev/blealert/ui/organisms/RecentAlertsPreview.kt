package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.core.alert.Alert
import by.vsdev.blealert.ui.molecules.AlertRow

@Composable
fun RecentAlertsPreview(alerts: List<Alert>, modifier: Modifier = Modifier, maxItems: Int = 5) {
    Column(modifier = modifier) {
        Text(
            text = "Recent alerts",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (alerts.isEmpty()) {
            Text(
                text = "No alerts yet",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            return@Column
        }
        alerts.asReversed().take(maxItems).forEach { alert -> AlertRow(alert = alert) }
    }
}
