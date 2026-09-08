package by.vsdev.blealert.feature.dashboard

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
import by.vsdev.blealert.ui.organisms.ConnectionStatusBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardScreen(modifier: Modifier = Modifier, viewModel: DashboardViewModel = koinViewModel()) {
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
