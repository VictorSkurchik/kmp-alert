"""BLE GATT peripheral wrapping `bless`, advertising the Alert service defined in protocol.py."""

from bless import (
    BlessServer,
    GATTAttributePermissions,
    GATTCharacteristicProperties,
)

from protocol import ALERT_NOTIFY_CHARACTERISTIC_UUID, ALERT_SERVICE_UUID


async def build_and_start() -> BlessServer:
    server = BlessServer(name="TinkerBoardAlert")

    await server.add_new_service(ALERT_SERVICE_UUID)
    await server.add_new_characteristic(
        ALERT_SERVICE_UUID,
        ALERT_NOTIFY_CHARACTERISTIC_UUID,
        GATTCharacteristicProperties.notify,
        None,
        GATTAttributePermissions.readable,
    )
    await server.start()

    print("BLE Alert peripheral advertising as 'TinkerBoardAlert'")
    return server


def notify(server: BlessServer, payload: bytes) -> None:
    characteristic = server.get_characteristic(ALERT_NOTIFY_CHARACTERISTIC_UUID)
    characteristic.value = bytearray(payload)
    server.update_value(ALERT_SERVICE_UUID, ALERT_NOTIFY_CHARACTERISTIC_UUID)


async def stop(server: BlessServer) -> None:
    await server.stop()
