package by.vsdev.blealert.feature.monitoring

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import by.vsdev.blealert.ui.organisms.ConnectionStatusBar
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MonitoringScreen(modifier: Modifier = Modifier, viewModel: MonitoringViewModel = koinViewModel()) {
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
