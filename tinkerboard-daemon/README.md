# Tinker Board BLE alert daemon

A simple BLE peripheral for the Asus Tinker Board (or any Linux/BlueZ board) that simulates a
motion sensor and pushes alerts to the Android/iOS app over BLE. Built with
[`bless`](https://github.com/kevincar/bless) on top of BlueZ's D-Bus GATT-server API.

**Hardware-verified** on a real ASUS Tinker Board running the official Debian 10 (buster) image
(Python 3.7.3, BlueZ 5.50): the daemon registers the GATT service/characteristic, starts
advertising as `TinkerBoardAlert`, and successfully calls `update_value()` on a timer without
errors. What hasn't been verified yet is a real BLE central (the Android/iOS app) actually
connecting and receiving a notification — that's the next check once a phone is available.

Two real findings from that hardware run, both already fixed here:

- **`bless==0.3.0` does not install on Python 3.7** — it requires `bleak>=1.1.1`, which itself
  requires Python 3.8+. Debian buster (this board's OS) ships Python 3.7.3, and buster is EOL so
  its apt repos 404 — there's no easy path to a newer Python via apt either. Use `bless==0.2.6`
  instead (pinned in `requirements.txt`); its `BlessServer` API (`add_new_service`,
  `add_new_characteristic`, `get_characteristic`, `update_value`, `start`/`stop`) is identical to
  what's used in `ble_peripheral.py`.
- **Python buffers stdout when it's not a TTY** (i.e. whenever you redirect to a log file, as any
  real deployment does) — `print()` output can sit unflushed for a long time, making the daemon
  look hung when it isn't. Always run with `python3 -u` (or `PYTHONUNBUFFERED=1`), as done below.

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
python3 -m venv .venv && source .venv/bin/activate  # if python3-venv isn't installable (e.g.
                                                     # EOL Debian buster with 404ing apt repos),
                                                     # skip venv and use `pip3 install --user` below
pip install -r requirements.txt
python3 -u main.py
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
Environment=PYTHONUNBUFFERED=1
ExecStart=/path/to/tinkerboard-daemon/.venv/bin/python3 -u /path/to/tinkerboard-daemon/main.py
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
- Confirmed working on Tinker Board's onboard Realtek Bluetooth adapter (BlueZ 5.50, Debian
  buster) with the default user account — no extra D-Bus policy configuration was needed for
  `add_new_service`/`start`/`update_value` to succeed.
