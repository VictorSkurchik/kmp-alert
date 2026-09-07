package by.vsdev.blealert.ui.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.core.alert.Alert
import by.vsdev.blealert.core.alert.AlertSeverity
import by.vsdev.blealert.ui.molecules.StatCard

@Composable
fun DashboardStatsGrid(alerts: List<Alert>, modifier: Modifier = Modifier) {
    val critical = alerts.count { it.severity == AlertSeverity.CRITICAL }
    val warning = alerts.count { it.severity == AlertSeverity.WARNING }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(label = "Total alerts", value = alerts.size.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Critical", value = critical.toString(), modifier = Modifier.weight(1f))
        StatCard(label = "Warning", value = warning.toString(), modifier = Modifier.weight(1f))
    }
}
