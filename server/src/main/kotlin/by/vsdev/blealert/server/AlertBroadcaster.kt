package by.vsdev.blealert.server

import by.vsdev.blealert.domain.Alert
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitAll
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

    // alertId -> (session -> that session's own ACK), so one client's ACK never satisfies another's.
    private val pendingAcks = ConcurrentHashMap<String, ConcurrentHashMap<DefaultWebSocketServerSession, CompletableDeferred<Unit>>>()

    fun register(session: DefaultWebSocketServerSession) {
        sessions.add(session)
        println("Client connected, ${sessions.size} session(s) active")
    }

    fun unregister(session: DefaultWebSocketServerSession) {
        sessions.remove(session)
        println("Client disconnected, ${sessions.size} session(s) active")
    }

    fun onAck(alertId: String, session: DefaultWebSocketServerSession) {
        if (pendingAcks[alertId]?.get(session)?.complete(Unit) == true) {
            println("ACK received for alert $alertId from a client")
        }
    }

    suspend fun broadcast(alert: Alert) {
        val targets = sessions.toList()
        println("Broadcasting ${alert.type} (${alert.severity}) to ${targets.size} session(s)")
        if (targets.isEmpty()) {
            telegram.sendFallback(alert)
            return
        }

        val perSessionAcks = ConcurrentHashMap<DefaultWebSocketServerSession, CompletableDeferred<Unit>>()
        targets.forEach { perSessionAcks[it] = CompletableDeferred() }
        pendingAcks[alert.id] = perSessionAcks

        val frame = Frame.Text(json.encodeToString(AlertEnvelope.serializer(), AlertEnvelope(payload = alert)))
        targets.forEach { session ->
            runCatching { session.send(frame) }
        }

        val allAcked = withTimeoutOrNull(ackTimeout) { perSessionAcks.values.awaitAll() }
        pendingAcks.remove(alert.id)
        if (allAcked == null) {
            println("Not all clients acked alert ${alert.id} within $ackTimeout, falling back to Telegram")
            telegram.sendFallback(alert)
        }
    }
}

@Serializable
data class AlertEnvelope(val type: String = "alert", val payload: Alert)
