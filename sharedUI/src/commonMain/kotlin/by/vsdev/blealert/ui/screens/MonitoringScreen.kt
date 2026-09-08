package by.vsdev.blealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.ui.organisms.AlertHistoryList
import by.vsdev.blealert.ui.organisms.ConnectionStatusBar

@Composable
fun MonitoringScreen(viewModel: MonitoringViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        ConnectionStatusBar(
            state = uiState.connectionState,
            onDisconnect = viewModel::disconnect,
            onReconnect = viewModel::reconnect,
        )
        AlertHistoryList(alerts = uiState.alerts, modifier = Modifier.weight(1f))
    }
}
