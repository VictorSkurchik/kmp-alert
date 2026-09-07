package by.vsdev.blealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.ui.organisms.AlertHistoryList
import by.vsdev.blealert.ui.organisms.ConnectionStatusBar
import by.vsdev.blealert.ui.templates.MonitoringScaffold

@Composable
fun MonitoringScreen(viewModel: MonitoringViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MonitoringScaffold(title = "Monitoring") { modifier ->
        Column(modifier = modifier) {
            ConnectionStatusBar(state = uiState.connectionState, onDisconnect = viewModel::disconnect)
            AlertHistoryList(alerts = uiState.alerts, modifier = Modifier.weight(1f))
        }
    }
}
