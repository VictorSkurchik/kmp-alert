package by.vsdev.blealert.core.ble

import com.juul.kable.Peripheral
import com.juul.kable.PlatformAdvertisement
import com.juul.kable.State
import com.juul.kable.characteristicOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalUuidApi::class)
class BleAlertClient {

    private val alertCharacteristic = characteristicOf(
        Uuid.parse(BleUuids.ALERT_SERVICE),
        Uuid.parse(BleUuids.ALERT_NOTIFY_CHARACTERISTIC),
    )

    private var peripheral: Peripheral? = null

    fun observeConnectionState(advertisement: PlatformAdvertisement): Flow<BleConnectionState> {
        val peripheral = Peripheral(advertisement) {}.also { peripheral = it }
        return peripheral.state.map { state -> state.toBleConnectionState() }
    }

    fun observeAlertPayloads(): Flow<ByteArray> {
        val peripheral = requireNotNull(peripheral) { "Call observeConnectionState() first" }
        return peripheral.observe(alertCharacteristic)
            .catch { throw BleClientException(BleError.ConnectionLost) }
    }

    suspend fun connect() {
        val peripheral = requireNotNull(peripheral) { "Call observeConnectionState() first" }
        try {
            peripheral.connect()
        } catch (e: Exception) {
            throw BleClientException(e.toBleError())
        }
    }

    suspend fun disconnect() {
        peripheral?.disconnect()
    }

    private fun State.toBleConnectionState(): BleConnectionState = when (this) {
        is State.Connecting -> BleConnectionState.Connecting
        is State.Connected -> BleConnectionState.Connected
        is State.Disconnecting -> BleConnectionState.Connecting
        is State.Disconnected -> BleConnectionState.Disconnected(BleError.ConnectionLost)
    }
}

class BleClientException(val error: BleError) : Exception()
