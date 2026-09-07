import asyncio

from ble_peripheral import build_and_start, notify, stop
from simulator import run_manual_trigger_loop, run_motion_loop


async def main() -> None:
    server = await build_and_start()

    def on_alert(payload: bytes) -> None:
        notify(server, payload)

    try:
        await asyncio.gather(
            run_motion_loop(on_alert),
            run_manual_trigger_loop(on_alert),
        )
    finally:
        await stop(server)


if __name__ == "__main__":
    asyncio.run(main())
