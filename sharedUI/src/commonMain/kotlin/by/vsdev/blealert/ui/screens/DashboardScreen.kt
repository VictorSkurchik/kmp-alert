package by.vsdev.blealert.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.MonitoringViewModel
import by.vsdev.blealert.ui.organisms.ConnectionStatusBar
import by.vsdev.blealert.ui.organisms.DashboardStatsGrid
import by.vsdev.blealert.ui.organisms.RecentAlertsPreview

@Composable
fun DashboardScreen(viewModel: MonitoringViewModel, modifier: Modifier = Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxWidth()) {
        ConnectionStatusBar(
            state = uiState.connectionState,
            onDisconnect = viewModel::disconnect,
            onReconnect = viewModel::reconnect,
        )
        DashboardStatsGrid(
            alerts = uiState.alerts,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        RecentAlertsPreview(alerts = uiState.alerts, modifier = Modifier.fillMaxWidth())
    }
}
