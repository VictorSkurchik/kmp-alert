package by.vsdev.blealert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.vsdev.blealert.core.alert.NotificationPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonitoringViewModel(
    private val coordinator: RemoteAlertCoordinator,
    private val notificationPermissionManager: NotificationPermissionManager,
) : ViewModel() {

    val uiState: StateFlow<MonitoringUiState> = combine(
        coordinator.connectionState,
        coordinator.alertHistory.alerts,
    ) { connectionState, alerts ->
        MonitoringUiState(connectionState = connectionState, alerts = alerts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonitoringUiState())

    private val _notificationPermissionGranted = MutableStateFlow(notificationPermissionManager.hasPermission())
    val notificationPermissionGranted: StateFlow<Boolean> = _notificationPermissionGranted

    fun disconnect() = coordinator.disconnect()

    fun reconnect() = coordinator.reconnect()

    fun clearAlertHistory() = coordinator.alertHistory.clear()

    fun refreshNotificationPermissionState() {
        _notificationPermissionGranted.value = notificationPermissionManager.hasPermission()
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            _notificationPermissionGranted.value = notificationPermissionManager.requestPermission()
        }
    }
}
