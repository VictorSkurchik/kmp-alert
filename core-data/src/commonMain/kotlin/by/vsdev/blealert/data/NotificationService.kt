package by.vsdev.blealert.data

import by.vsdev.blealert.domain.Alert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

internal enum class RemoteConnectionState { IDLE, CONNECTING, CONNECTED, DISCONNECTED }

internal interface NotificationService {
    val connectionState: StateFlow<RemoteConnectionState>
    fun alerts(): Flow<Alert>
    suspend fun connect()
    suspend fun disconnect()
    fun acknowledge(alertId: String)
}
