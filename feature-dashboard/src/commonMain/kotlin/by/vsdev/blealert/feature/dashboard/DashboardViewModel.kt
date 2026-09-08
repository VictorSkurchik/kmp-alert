package by.vsdev.blealert.feature.dashboard

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

data class DashboardUiState(
    val connectionState: ConnectionState = ConnectionState.IDLE,
    val alerts: List<Alert> = emptyList(),
)

class DashboardViewModel(private val repository: AlertRepository) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = combine(
        repository.connectionState,
        repository.alertHistory,
    ) { connectionState, alerts ->
        DashboardUiState(connectionState = connectionState, alerts = alerts)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun disconnect() = repository.disconnect()

    fun reconnect() = repository.reconnect()
}

val dashboardModule = module {
    viewModel { DashboardViewModel(get()) }
}
