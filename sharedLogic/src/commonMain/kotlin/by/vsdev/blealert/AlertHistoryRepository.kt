package by.vsdev.blealert

import by.vsdev.blealert.core.alert.Alert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AlertHistoryRepository {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    fun record(alert: Alert) {
        _alerts.update { it + alert }
    }
}
