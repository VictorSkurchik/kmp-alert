"""BLE Alert wire protocol.

Must stay in sync by hand with core-ble's BleUuids and core-alert's
AlertPayloadParser in the Kotlin Multiplatform app — there is no shared
source of truth between Python and Kotlin here.
"""

ALERT_SERVICE_UUID = "d99fe72d-a659-4504-a4c5-2d2999747b69"
ALERT_NOTIFY_CHARACTERISTIC_UUID = "fb947ba4-02f8-4c16-ae92-158eac164fe3"

PROTOCOL_VERSION = 1

ALERT_TYPE_MOTION = 0
ALERT_TYPE_SOS = 1

SEVERITY_INFO = 0
SEVERITY_WARNING = 1
SEVERITY_CRITICAL = 2


def encode_alert(alert_type: int, severity: int) -> bytes:
    return bytes([PROTOCOL_VERSION, alert_type, severity])
