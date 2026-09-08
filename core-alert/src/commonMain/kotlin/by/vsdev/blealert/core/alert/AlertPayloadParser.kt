package by.vsdev.blealert.core.alert

private const val PROTOCOL_VERSION: Byte = 1

fun parseAlertPayload(payload: ByteArray, sourceDeviceId: String): Alert? {
    if (payload.size != 3) return null
    if (payload[0] != PROTOCOL_VERSION) return null

    val type = when (payload[1].toInt()) {
        0 -> AlertType.MOTION
        1 -> AlertType.SOS
        else -> return null
    }
    val severity = when (payload[2].toInt()) {
        0 -> AlertSeverity.INFO
        1 -> AlertSeverity.WARNING
        2 -> AlertSeverity.CRITICAL
        else -> return null
    }

    val now = currentTimeMillis()
    return Alert(
        id = "$sourceDeviceId-$now",
        type = type,
        severity = severity,
        receivedAt = now,
        sourceDeviceId = sourceDeviceId,
    )
}
