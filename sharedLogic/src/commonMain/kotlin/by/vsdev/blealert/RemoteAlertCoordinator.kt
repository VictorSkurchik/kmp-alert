package by.vsdev.blealert

import by.vsdev.blealert.core.alert.AlertNotifier
import by.vsdev.blealert.core.notification.NotificationService
import by.vsdev.blealert.core.notification.RemoteConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Bridges the backend-pushed [NotificationService] to the same [AlertHistoryRepository]/
 * [AlertNotifier] the old BLE-backed MonitoringRepository used, so the rest of the app (history,
 * local OS notifications) didn't need to change - only the data source did.
 */
class RemoteAlertCoordinator(
    private val scope: CoroutineScope,
    private val notificationService: NotificationService,
    private val alertNotifier: AlertNotifier,
    val alertHistory: AlertHistoryRepository,
) {
    val connectionState: StateFlow<ConnectionUiState> = notificationService.connectionState
        .map { it.toConnectionUiState() }
        .stateIn(scope, SharingStarted.Eagerly, ConnectionUiState.IDLE)

    fun start() {
        notificationService.alerts()
            .onEach { alert ->
                alertHistory.record(alert)
                alertNotifier.notify(alert)
                notificationService.acknowledge(alert.id)
            }
            .launchIn(scope)
        scope.launch { notificationService.connect() }
    }

    fun reconnect() {
        scope.launch { notificationService.connect() }
    }

    fun disconnect() {
        scope.launch { notificationService.disconnect() }
    }
}

private fun RemoteConnectionState.toConnectionUiState(): ConnectionUiState = when (this) {
    RemoteConnectionState.IDLE -> ConnectionUiState.IDLE
    RemoteConnectionState.CONNECTING -> ConnectionUiState.CONNECTING
    RemoteConnectionState.CONNECTED -> ConnectionUiState.CONNECTED
    RemoteConnectionState.DISCONNECTED -> ConnectionUiState.DISCONNECTED
}
