package by.vsdev.blealert

import by.vsdev.blealert.core.alert.Alert

enum class ConnectionUiState { IDLE, SCANNING, CONNECTING, CONNECTED, DISCONNECTED }

data class MonitoringUiState(
    val connectionState: ConnectionUiState = ConnectionUiState.IDLE,
    val alerts: List<Alert> = emptyList(),
)
