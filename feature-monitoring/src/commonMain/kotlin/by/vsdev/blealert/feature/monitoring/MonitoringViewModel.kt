package by.vsdev.blealert.feature.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertRepository
import by.vsdev.blealert.domain.ConnectionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

data class MonitoringUiState(
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val alerts: List<Alert> = emptyList(),
)

class MonitoringViewModel(private val repository: AlertRepository) : ViewModel() {

    val uiState: StateFlow<MonitoringUiState> = combine(
        repository.connectionState,
        repository.alertHistory,
    ) { connectionState, alerts ->
        MonitoringUiState(connectionState = connectionState, alerts = alerts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonitoringUiState())

    fun disconnect() = repository.disconnect()

    fun reconnect() = repository.reconnect()
}

val monitoringModule = module {
    viewModel { MonitoringViewModel(get()) }
}
