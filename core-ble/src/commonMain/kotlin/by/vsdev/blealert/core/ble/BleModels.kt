package by.vsdev.blealert.core.ble

import com.juul.kable.UnmetRequirementException
import com.juul.kable.UnmetRequirementReason

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

/**
 * Kable throws plain [IllegalStateException]/[UnmetRequirementException] rather than typed
 * per-cause exceptions - map the two causes we can actually hit (missing runtime permission,
 * Bluetooth adapter off) to our domain errors instead of letting them crash the process.
 */
fun Throwable.toBleError(): BleError = when {
    this is UnmetRequirementException && reason == UnmetRequirementReason.BluetoothDisabled ->
        BleError.BluetoothDisabled
    this is IllegalStateException && message?.contains("permission", ignoreCase = true) == true ->
        BleError.PermissionDenied
    else -> BleError.Unknown(message)
}
