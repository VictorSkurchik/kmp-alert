package by.vsdev.blealert.feature.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.ui.molecules.AlertRow
import kmp_ble_alert.core_ui.generated.resources.Res as CoreUiRes
import kmp_ble_alert.core_ui.generated.resources.no_alerts_yet
import kmp_ble_alert.feature_dashboard.generated.resources.Res
import kmp_ble_alert.feature_dashboard.generated.resources.recent_alerts_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun RecentAlertsPreview(alerts: List<Alert>, modifier: Modifier = Modifier, maxItems: Int = 5) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.recent_alerts_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (alerts.isEmpty()) {
            Text(
                text = stringResource(CoreUiRes.string.no_alerts_yet),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            return@Column
        }
        alerts.asReversed().take(maxItems).forEach { alert -> AlertRow(alert = alert) }
    }
}
