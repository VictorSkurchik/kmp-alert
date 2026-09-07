"""Simulated motion sensor (periodic) plus a manual SOS trigger (stdin) for demos."""

import asyncio
import random
from typing import Callable

from protocol import (
    ALERT_TYPE_MOTION,
    ALERT_TYPE_SOS,
    SEVERITY_CRITICAL,
    SEVERITY_WARNING,
    encode_alert,
)

OnAlert = Callable[[bytes], None]


async def run_motion_loop(on_alert: OnAlert) -> None:
    while True:
        await asyncio.sleep(random.uniform(15, 30))
        on_alert(encode_alert(ALERT_TYPE_MOTION, SEVERITY_WARNING))
        print("Sent MOTION alert")


async def run_manual_trigger_loop(on_alert: OnAlert) -> None:
    loop = asyncio.get_event_loop()
    while True:
        await loop.run_in_executor(None, input, "Press Enter to trigger an SOS alert...\n")
        on_alert(encode_alert(ALERT_TYPE_SOS, SEVERITY_CRITICAL))
        print("Sent SOS alert")
