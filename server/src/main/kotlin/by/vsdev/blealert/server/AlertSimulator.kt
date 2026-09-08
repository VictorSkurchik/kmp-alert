package by.vsdev.blealert.server

import by.vsdev.blealert.domain.Alert
import by.vsdev.blealert.domain.AlertSeverity
import by.vsdev.blealert.domain.AlertType
import by.vsdev.blealert.domain.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val DEMO_HOUSEHOLD = "demo-household"

private val SCENARIOS: Map<AlertType, AlertSeverity> = mapOf(
    AlertType.WOKE_UP to AlertSeverity.INFO,
    AlertType.MOTION to AlertSeverity.INFO,
    AlertType.LONG_BATHROOM_TIME to AlertSeverity.WARNING,
    AlertType.NOT_RETURNED_HOME to AlertSeverity.WARNING,
    AlertType.NO_ACTIVITY to AlertSeverity.WARNING,
    AlertType.DEVICE_OFFLINE to AlertSeverity.CRITICAL,
    AlertType.SOS to AlertSeverity.CRITICAL,
)

/**
 * Stands in for the retired physical BLE sensor: periodically fabricates a "Wi-Fi Sensing" event
 * for the single demo household, plus a manual-trigger path for deterministic demos/tests.
 */
class AlertSimulator(
    private val scope: CoroutineScope,
    private val broadcaster: AlertBroadcaster,
) {
    fun start() {
        scope.launch {
            while (isActive) {
                delay((MIN_INTERVAL_MILLIS..MAX_INTERVAL_MILLIS).random())
                broadcaster.broadcast(randomAlert())
            }
        }
    }

    fun scenarioByKey(key: String?): Alert? {
        val type = key?.let { k -> AlertType.entries.find { it.name.equals(k, ignoreCase = true) } }
        return type?.let(::alertOf)
    }

    private fun randomAlert(): Alert = alertOf(SCENARIOS.keys.random())

    private fun alertOf(type: AlertType): Alert {
        val now = currentTimeMillis()
        return Alert(
            id = "$DEMO_HOUSEHOLD-${type.name}-$now",
            type = type,
            severity = SCENARIOS.getValue(type),
            receivedAt = now,
            sourceDeviceId = DEMO_HOUSEHOLD,
        )
    }

    private companion object {
        const val MIN_INTERVAL_MILLIS = 30_000L
        const val MAX_INTERVAL_MILLIS = 90_000L
    }
}
