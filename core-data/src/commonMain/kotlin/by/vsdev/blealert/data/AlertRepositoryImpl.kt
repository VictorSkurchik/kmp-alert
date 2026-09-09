package by.vsdev.blealert.data

import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertNotifier
import by.vsdev.blealert.domain.AlertRepository
import by.vsdev.blealert.domain.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Bridges the backend-pushed [NotificationService] into the domain-facing [AlertRepository]
 * contract, funnelling alerts through [AlertHistoryStore] (in-memory cache) and [AlertNotifier]
 * (local OS notification) as they arrive.
 */
internal class AlertRepositoryImpl(
    private val scope: CoroutineScope,
    private val notificationService: NotificationService,
    private val alertNotifier: AlertNotifier,
) : AlertRepository {

    private val historyStore = AlertHistoryStore()

    override val alertHistory: StateFlow<List<Alert>> = historyStore.alerts

    override val connectionState: StateFlow<ConnectionState> = notificationService.connectionState
        .map { it.toConnectionState() }
        .stateIn(scope, SharingStarted.Eagerly, ConnectionState.IDLE)

    init {
        notificationService.alerts()
            .onEach { alert ->
                historyStore.record(alert)
                // A single alert's local notification failing (e.g. a denied POST_NOTIFICATIONS
                // permission on Android) must never take down this collector - that would silently
                // stop acking and recording every alert for the rest of the process's lifetime.
                runCatching { alertNotifier.notify(alert) }
                notificationService.acknowledge(alert.id)
            }
            .launchIn(scope)
        scope.launch { notificationService.connect() }
    }

    override fun reconnect() {
        scope.launch { notificationService.connect() }
    }

    override fun disconnect() {
        scope.launch { notificationService.disconnect() }
    }

    override fun clearHistory() = historyStore.clear()
}

private fun RemoteConnectionState.toConnectionState(): ConnectionState = when (this) {
    RemoteConnectionState.IDLE -> ConnectionState.IDLE
    RemoteConnectionState.CONNECTING -> ConnectionState.CONNECTING
    RemoteConnectionState.CONNECTED -> ConnectionState.CONNECTED
    RemoteConnectionState.DISCONNECTED -> ConnectionState.DISCONNECTED
}
