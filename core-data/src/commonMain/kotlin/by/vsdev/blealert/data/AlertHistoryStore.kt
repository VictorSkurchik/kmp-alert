package by.vsdev.blealert.data

import by.vsdev.blealert.domain.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

internal class AlertHistoryStore {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun record(alert: Alert) {
        _alerts.update { it + alert }
    }

    fun clear() {
        _alerts.value = emptyList()
    }
}
