package by.vsdev.blealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.ui.molecules.SettingsRow
import by.vsdev.blealert.ui.organisms.SettingsSection
import kmp_ble_alert.sharedui.generated.resources.Res
import kmp_ble_alert.sharedui.generated.resources.settings_row_alert_notifications_title
import kmp_ble_alert.sharedui.generated.resources.settings_row_allowed
import kmp_ble_alert.sharedui.generated.resources.settings_row_alerts_stored
import kmp_ble_alert.sharedui.generated.resources.settings_row_clear_history_title
import kmp_ble_alert.sharedui.generated.resources.settings_row_not_allowed
import kmp_ble_alert.sharedui.generated.resources.settings_section_alert_history
import kmp_ble_alert.sharedui.generated.resources.settings_section_notifications
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(viewModel: MonitoringViewModel, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) { viewModel.refreshNotificationPermissionState() }
    val notificationsGranted by viewModel.notificationPermissionGranted.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                subtitle = stringResource(Res.string.settings_row_alerts_stored, uiState.alerts.size),
                onClick = viewModel::clearAlertHistory,
            )
        }
    }
}
