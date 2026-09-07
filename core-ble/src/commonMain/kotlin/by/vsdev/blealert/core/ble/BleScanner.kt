package by.vsdev.blealert.core.ble

import com.juul.kable.PlatformAdvertisement
import com.juul.kable.Scanner
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalUuidApi::class)
class BleScanner {

    private val scanner = Scanner {
        filters {
            match {
                services = listOf(Uuid.parse(BleUuids.ALERT_SERVICE))
            }
        }
    }

    private val advertisementsById = mutableMapOf<String, PlatformAdvertisement>()

    fun scan(): Flow<BleDevice> =
        scanner.advertisements.map { advertisement ->
            val id = advertisement.identifier.toString()
            advertisementsById[id] = advertisement
            BleDevice(id = id, name = advertisement.name)
        }

    fun advertisementFor(deviceId: String): PlatformAdvertisement? = advertisementsById[deviceId]
}
