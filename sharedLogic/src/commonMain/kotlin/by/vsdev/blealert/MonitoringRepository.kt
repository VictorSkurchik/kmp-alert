package by.vsdev.blealert

import by.vsdev.blealert.core.alert.Alert
import by.vsdev.blealert.core.alert.AlertNotifier
import by.vsdev.blealert.core.alert.deviceOfflineAlert
import by.vsdev.blealert.core.alert.parseAlertPayload
import by.vsdev.blealert.core.ble.BleAlertClient
import by.vsdev.blealert.core.ble.BleConnectionState
import by.vsdev.blealert.core.ble.BleDevice
import by.vsdev.blealert.core.ble.BleScanner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Owns the BLE session and alert dispatch for the lifetime of the process (not tied to any
 * ViewModel/UI lifecycle) so a backgrounded UI doesn't interrupt monitoring — the Android
 * foreground service and the UI layer both observe the same instance.
 */
class MonitoringRepository(
    private val scope: CoroutineScope,
    private val scanner: BleScanner,
    private val bleClient: BleAlertClient,
    private val alertNotifier: AlertNotifier,
    val alertHistory: AlertHistoryRepository,
) {

    private val _connectionState = MutableStateFlow(ConnectionUiState.IDLE)
    val connectionState: StateFlow<ConnectionUiState> = _connectionState

    private val _discoveredDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BleDevice>> = _discoveredDevices

    private var hasConnectedOnce = false

    fun startScanning() {
        _connectionState.value = ConnectionUiState.SCANNING
        scanner.scan()
            .onEach { device ->
                _discoveredDevices.update { current ->
                    if (current.any { it.id == device.id }) current else current + device
                }
            }
            .launchIn(scope)
    }

    fun connect(device: BleDevice) {
        val advertisement = scanner.advertisementFor(device.id) ?: return
        _connectionState.value = ConnectionUiState.CONNECTING

        bleClient.observeConnectionState(advertisement)
            .onEach { state -> onConnectionState(device.id, state) }
            .launchIn(scope)
        bleClient.observeAlertPayloads()
            .onEach { payload -> onPayload(device.id, payload) }
            .launchIn(scope)

        scope.launch { bleClient.connect() }
    }

    fun disconnect() {
        scope.launch { bleClient.disconnect() }
    }

    private fun onConnectionState(deviceId: String, state: BleConnectionState) {
        when (state) {
            BleConnectionState.Connecting -> _connectionState.value = ConnectionUiState.CONNECTING
            BleConnectionState.Connected -> {
                hasConnectedOnce = true
                _connectionState.value = ConnectionUiState.CONNECTED
            }
            is BleConnectionState.Disconnected -> {
                _connectionState.value = ConnectionUiState.DISCONNECTED
                if (hasConnectedOnce) {
                    hasConnectedOnce = false
                    recordAndNotify(deviceOfflineAlert(deviceId))
                }
            }
        }
    }

    private fun onPayload(deviceId: String, payload: ByteArray) {
        val alert = parseAlertPayload(payload, deviceId) ?: return
        recordAndNotify(alert)
    }

    private fun recordAndNotify(alert: Alert) {
        alertHistory.record(alert)
        alertNotifier.notify(alert)
    }
}
