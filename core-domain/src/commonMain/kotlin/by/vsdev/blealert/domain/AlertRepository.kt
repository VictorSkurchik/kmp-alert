package by.vsdev.blealert.domain

import kotlinx.coroutines.flow.StateFlow

interface AlertRepository {
    val connectionState: StateFlow<ConnectionState>
    val alertHistory: StateFlow<List<Alert>>
    fun reconnect()
    fun disconnect()
    fun clearHistory()
}
