package by.vsdev.blealert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.vsdev.blealert.core.ble.BleDevice
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MonitoringViewModel(private val repository: MonitoringRepository) : ViewModel() {

    val uiState: StateFlow<MonitoringUiState> = combine(
        repository.connectionState,
        repository.alertHistory.alerts,
    ) { connectionState, alerts ->
        MonitoringUiState(connectionState = connectionState, alerts = alerts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonitoringUiState())

    val devices: StateFlow<List<BleDevice>> = repository.discoveredDevices

    fun startScanning() = repository.startScanning()

    fun connect(device: BleDevice) = repository.connect(device)

    fun disconnect() = repository.disconnect()
}
