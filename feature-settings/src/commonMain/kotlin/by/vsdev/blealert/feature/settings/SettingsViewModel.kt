package by.vsdev.blealert.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.vsdev.blealert.domain.AlertRepository
import by.vsdev.blealert.domain.NotificationPermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

class SettingsViewModel(
    private val alertRepository: AlertRepository,
    private val notificationPermissionManager: NotificationPermissionManager,
) : ViewModel() {

    val alertsStoredCount: StateFlow<Int> = alertRepository.alertHistory
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _notificationPermissionGranted = MutableStateFlow(notificationPermissionManager.hasPermission())
    val notificationPermissionGranted: StateFlow<Boolean> = _notificationPermissionGranted

    fun clearAlertHistory() = alertRepository.clearHistory()

    fun refreshNotificationPermissionState() {
        _notificationPermissionGranted.value = notificationPermissionManager.hasPermission()
    }

    fun requestNotificationPermission() {
        viewModelScope.launch {
            _notificationPermissionGranted.value = notificationPermissionManager.requestPermission()
        }
    }
}

val settingsModule = module {
    viewModel { SettingsViewModel(get(), get()) }
}
