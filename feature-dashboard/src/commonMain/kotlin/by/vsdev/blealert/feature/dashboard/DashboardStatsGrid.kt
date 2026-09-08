package by.vsdev.blealert.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertSeverity
import kmp_ble_alert.feature_dashboard.generated.resources.Res
import kmp_ble_alert.feature_dashboard.generated.resources.stat_critical
import kmp_ble_alert.feature_dashboard.generated.resources.stat_total_alerts
import kmp_ble_alert.feature_dashboard.generated.resources.stat_warning
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DashboardStatsGrid(alerts: List<Alert>, modifier: Modifier = Modifier) {
    val critical = alerts.count { it.severity == AlertSeverity.CRITICAL }
    val warning = alerts.count { it.severity == AlertSeverity.WARNING }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            label = stringResource(Res.string.stat_total_alerts),
            value = alerts.size.toString(),
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        StatCard(
            label = stringResource(Res.string.stat_critical),
            value = critical.toString(),
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
        StatCard(
            label = stringResource(Res.string.stat_warning),
            value = warning.toString(),
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
