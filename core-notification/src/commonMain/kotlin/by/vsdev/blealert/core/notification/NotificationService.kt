package by.vsdev.blealert.core.notification

import by.vsdev.blealert.core.alert.Alert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

enum class RemoteConnectionState { IDLE, CONNECTING, CONNECTED, DISCONNECTED }

interface NotificationService {
    val connectionState: StateFlow<RemoteConnectionState>
    fun alerts(): Flow<Alert>
    suspend fun connect()
    suspend fun disconnect()
    fun acknowledge(alertId: String)
}
