package by.vsdev.blealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.core.ble.BleUuids
import by.vsdev.blealert.ui.molecules.SettingsRow
import by.vsdev.blealert.ui.organisms.SettingsSection

@Composable
fun SettingsScreen(viewModel: MonitoringViewModel, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { viewModel.refreshNotificationPermissionState() }
    val notificationsGranted by viewModel.notificationPermissionGranted.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        SettingsSection(title = "Notifications") {
            SettingsRow(
                title = "Alert notifications",
                subtitle = if (notificationsGranted) "Allowed" else "Not allowed — tap to enable",
                onClick = if (notificationsGranted) null else viewModel::requestNotificationPermission,
            )
        }
        SettingsSection(title = "Alert history") {
            SettingsRow(
                title = "Clear alert history",
                subtitle = "${uiState.alerts.size} alerts stored",
                onClick = viewModel::clearAlertHistory,
            )
        }
        SettingsSection(title = "Device") {
            SettingsRow(title = "Alert service UUID", subtitle = BleUuids.ALERT_SERVICE)
            SettingsRow(title = "Alert characteristic UUID", subtitle = BleUuids.ALERT_NOTIFY_CHARACTERISTIC)
        }
    }
}
