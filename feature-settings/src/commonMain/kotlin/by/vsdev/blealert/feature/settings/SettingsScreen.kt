package by.vsdev.blealert.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kmp_ble_alert.feature_settings.generated.resources.Res
import kmp_ble_alert.feature_settings.generated.resources.settings_row_alert_notifications_title
import kmp_ble_alert.feature_settings.generated.resources.settings_row_allowed
import kmp_ble_alert.feature_settings.generated.resources.settings_row_alerts_stored
import kmp_ble_alert.feature_settings.generated.resources.settings_row_clear_history_title
import kmp_ble_alert.feature_settings.generated.resources.settings_row_not_allowed
import kmp_ble_alert.feature_settings.generated.resources.settings_section_alert_history
import kmp_ble_alert.feature_settings.generated.resources.settings_section_notifications
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = koinViewModel()) {
    LaunchedEffect(Unit) { viewModel.refreshNotificationPermissionState() }
    val notificationsGranted by viewModel.notificationPermissionGranted.collectAsStateWithLifecycle()
    val alertsStoredCount by viewModel.alertsStoredCount.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        SettingsSection(title = stringResource(Res.string.settings_section_notifications)) {
            SettingsRow(
                title = stringResource(Res.string.settings_row_alert_notifications_title),
                subtitle = stringResource(
                    if (notificationsGranted) {
                        Res.string.settings_row_allowed
                    } else {
                        Res.string.settings_row_not_allowed
                    },
                ),
                onClick = if (notificationsGranted) null else viewModel::requestNotificationPermission,
            )
        }
        SettingsSection(title = stringResource(Res.string.settings_section_alert_history)) {
            SettingsRow(
                title = stringResource(Res.string.settings_row_clear_history_title),
                subtitle = stringResource(Res.string.settings_row_alerts_stored, alertsStoredCount),
                onClick = viewModel::clearAlertHistory,
            )
        }
    }
}
