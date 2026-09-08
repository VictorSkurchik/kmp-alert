# Tinker Board BLE alert daemon

A simple BLE peripheral for the Asus Tinker Board (or any Linux/BlueZ board) that simulates a
motion sensor and pushes alerts to the Android/iOS app over BLE. Built with
[`bless`](https://github.com/kevincar/bless) on top of BlueZ's D-Bus GATT-server API.

**Hardware-verified** on a real ASUS Tinker Board running the official Debian 10 (buster) image
(Python 3.7.3, BlueZ 5.50): the daemon registers the GATT service/characteristic, starts
advertising as `TinkerBoardAlert`, and successfully calls `update_value()` on a timer without
errors.

**Known hardware limitation — the onboard Realtek chip does not actually transmit BLE
advertisements.** Tested end-to-end against a real Android phone and isolated with `btmon`/raw
HCI: every software layer (`bless` → BlueZ D-Bus `LEAdvertisingManager1` → BlueZ mgmt → raw
`hcitool cmd` HCI commands sent directly to the controller with `bluetoothd` stopped) reports
success for `LE Set Advertising Parameters`/`LE Set Advertising Data`/`LE Set Advertise Enable`,
yet the phone's Bluetooth scanner (checked via `adb shell dumpsys bluetooth_manager`, both with our
service-UUID filter and with no filter at all) never sees a single packet — even standing right
next to the board. The same board's **classic Bluetooth** is confirmed working (phone sees
`TinkerBoardAlert` in its classic Bluetooth device list after `hciconfig hci0 piscan`), and the
board's own BLE **scanning** (RX) works fine too (`bluetoothctl scan on` sees nearby real devices).
So the radio/antenna and RX path are fine — this is specifically a broken/incomplete **LE
advertising (TX)** implementation in this chip's firmware (RTL8723BS, WiFi+BT combo over UART,
`rtk_hciattach`/`rtk_h5`), not a bug in `bless`, BlueZ, or this daemon's code. WiFi/BT coexistence
and connectable-vs-non-connectable advertising type were both tested and ruled out as the cause.

**Practical fix: use a USB BLE dongle instead of the onboard adapter.** Plug one in, find its
`hciN` index (`hciconfig -a`), and either make it the default controller or point `bless` at it
explicitly (`BlessServer(name=..., adapter="hciN")` — see `ble_peripheral.py`). This isn't
verified yet (no dongle was available during this session) but is the standard remedy for this
class of Realtek UART BT chip issue, since it swaps out the broken firmware/TX path entirely.

Connecting a real Android/iOS client once advertising actually reaches the air is the next
verification step.

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

## Re-diagnosing "advertising registers but nothing shows up on a scanner"

If you swap in a USB dongle (or a different board) and hit the same symptom, this is the
isolation sequence that was actually used to confirm the onboard-chip limitation above — cheapest
and most decisive checks first:

1. **Classic Bluetooth sanity check** (confirms the radio/antenna/TX path isn't just entirely
   dead): `sudo hciconfig hci0 piscan`, then check a phone's Bluetooth settings for the board's
   name in its classic device list.
2. **Unfiltered scan from the client**, not just a filtered one — a filtered scan finding nothing
   is ambiguous (wrong UUID in the payload vs. nothing transmitting at all); temporarily scanning
   with no service-UUID filter distinguishes the two.
3. **`btmon`** (BlueZ's own HCI sniffer) around a fresh `bless`/`btmgmt add-adv` registration —
   confirms whether `bluetoothd` actually issues `LE Set Advertising Data` at all (in the failure
   seen here, it never did, even though `Advertising Added` fired and `RegisterAdvertisement`
   returned success).
4. **Raw HCI, `bluetoothd` stopped**: `sudo systemctl stop bluetooth && sudo hciconfig hci0 up`,
   then `sudo hcitool -i hci0 cmd 0x08 0x0006 <params>` / `... 0x0008 <data>` / `... 0x000a 01`
   directly. If these report `Status: Success (0x00)` at every step and a client *still* sees
   nothing, BlueZ/D-Bus/mgmt are all cleared — it's the controller/firmware.
5. `sudo hcitool -i hci0 cmd 0x08 0x0003` (LE Read Supported States) — a quick sanity check on
   what advertising states the firmware itself claims to support, though don't over-trust it: on
   this board it under-reported support (only bit 0), yet switching advertising type to match what
   it claimed didn't fix anything either, so a firmware TX-path bug, not just a type mismatch, is
   the more likely explanation once raw HCI already succeeds with no client-side result.
- Confirmed working on Tinker Board's onboard Realtek Bluetooth adapter (BlueZ 5.50, Debian
  buster) with the default user account — no extra D-Bus policy configuration was needed for
  `add_new_service`/`start`/`update_value` to succeed.
