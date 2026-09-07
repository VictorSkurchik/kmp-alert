# Tinker Board BLE alert daemon

A simple BLE peripheral for the Asus Tinker Board (or any Linux/BlueZ board) that simulates a
motion sensor and pushes alerts to the Android/iOS app over BLE. Built with
[`bless`](https://github.com/kevincar/bless) on top of BlueZ's D-Bus GATT-server API.

**Not hardware-tested.** This was developed on a Mac with no BlueZ/Tinker Board available. The
`bless` API calls used here (`BlessServer`, `add_new_service`, `add_new_characteristic`, `start`,
`get_characteristic`, `.value =`, `update_value`) were confirmed by installing `bless==0.3.0` and
inspecting its real signatures locally — but only against its macOS/CoreBluetooth backend, since
that's what installs on a Mac. The actual advertising/notification behavior against BlueZ still
needs to be verified on real Linux+BlueZ hardware.

## Protocol

See `protocol.py` — must be kept in sync by hand with `core-ble`'s `BleUuids` and `core-alert`'s
`AlertPayloadParser` in the Kotlin app:

- Service UUID: `d99fe72d-a659-4504-a4c5-2d2999747b69`
- Alert-Notify characteristic UUID: `fb947ba4-02f8-4c16-ae92-158eac164fe3` (Notify)
- Payload: 3 bytes — `[version=1, alertType, severity]`
  - `alertType`: `0` = MOTION, `1` = SOS
  - `severity`: `0` = INFO, `1` = WARNING, `2` = CRITICAL

## Setup (on the Tinker Board / a Linux box with BlueZ)

```bash
sudo apt-get install bluez
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
python3 main.py
```

Every 15-30s the daemon sends a simulated `MOTION` alert. Press Enter in the terminal to fire a
one-off `SOS` alert on demand (useful for demoing the critical-alert path).

## Running as a systemd service

```ini
# /etc/systemd/system/ble-alert-daemon.service
[Unit]
Description=BLE Alert daemon
After=bluetooth.target

[Service]
ExecStart=/path/to/tinkerboard-daemon/.venv/bin/python3 /path/to/tinkerboard-daemon/main.py
Restart=on-failure
User=pi

[Install]
WantedBy=multi-user.target
```

## Known BlueZ/bless caveats

- `bless` requires BlueZ to already be installed and running (`bluetoothd`) — `pip install bless`
  does not install it.
- `DBus.Error.AccessDenied — not allowed to own the service`: the process needs D-Bus system-bus
  permission to register a GATT service/advertisement; run as a user/policy that's allowed to own
  the BlueZ D-Bus name (root, or a configured D-Bus policy), or run as a systemd service (as above).
- `Failed to register advertisement`: a known BlueZ D-Bus registration failure mode in some BlueZ
  versions/configs — if hit, try restarting `bluetoothd` and re-running.
- `bless`'s BlueZ backend talks to BlueZ's standard D-Bus GATT/advertising API with no
  Raspberry-Pi-specific code, so it should work on Tinker Board's BlueZ stack the same way, but
  this hasn't been confirmed by the `bless` maintainers on non-Raspberry-Pi ARM boards specifically.
