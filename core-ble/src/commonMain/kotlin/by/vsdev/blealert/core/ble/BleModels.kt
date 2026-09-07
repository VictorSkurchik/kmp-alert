package by.vsdev.blealert.core.ble

data class BleDevice(
    val id: String,
    val name: String?,
)

sealed class BleConnectionState {
    data object Connecting : BleConnectionState()
    data object Connected : BleConnectionState()
    data class Disconnected(val error: BleError?) : BleConnectionState()
}

sealed class BleError {
    data object PermissionDenied : BleError()
    data object BluetoothDisabled : BleError()
    data object ConnectionLost : BleError()
    data class Unknown(val message: String?) : BleError()
}
