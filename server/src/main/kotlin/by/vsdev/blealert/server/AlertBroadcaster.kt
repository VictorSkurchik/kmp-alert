package by.vsdev.blealert.server

import by.vsdev.blealert.domain.Alert
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration

/**
 * Pushes [Alert]s to every connected WebSocket session and waits [ackTimeout] for a client ACK.
 * If nobody acknowledges in time (or nobody is connected at all), falls back to Telegram - this
 * is the whole point of the MVP: delivery survives a dead/backgrounded app or flaky push.
 */
class AlertBroadcaster(
    private val scope: CoroutineScope,
    private val ackTimeout: Duration,
    private val telegram: TelegramNotifier,
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private val sessions = CopyOnWriteArrayList<DefaultWebSocketServerSession>()
    private val pendingAcks = ConcurrentHashMap<String, CompletableDeferred<Unit>>()

    fun register(session: DefaultWebSocketServerSession) {
        sessions.add(session)
        println("Client connected, ${sessions.size} session(s) active")
    }

    fun unregister(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
        println("Client disconnected, ${sessions.size} session(s) active")
    }

    fun onAck(alertId: String) {
        if (pendingAcks.remove(alertId)?.complete(Unit) == true) {
            println("ACK received for alert $alertId")
        }
    }

    suspend fun broadcast(alert: Alert) {
        println("Broadcasting ${alert.type} (${alert.severity}) to ${sessions.size} session(s)")
        if (sessions.isEmpty()) {
            telegram.sendFallback(alert)
            return
        }

        val deferred = CompletableDeferred<Unit>()
        pendingAcks[alert.id] = deferred
        val frame = Frame.Text(json.encodeToString(AlertEnvelope.serializer(), AlertEnvelope(payload = alert)))
        sessions.forEach { session ->
            runCatching { session.send(frame) }
        }

        val acked = withTimeoutOrNull(ackTimeout) { deferred.await() }
        pendingAcks.remove(alert.id)
        if (acked == null) {
            println("No ACK for alert ${alert.id} within $ackTimeout, falling back to Telegram")
            telegram.sendFallback(alert)
        }
    }
}

@Serializable
data class AlertEnvelope(val type: String = "alert", val payload: Alert)
