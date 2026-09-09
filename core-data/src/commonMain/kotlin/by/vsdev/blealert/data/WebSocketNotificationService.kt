package by.vsdev.blealert.data

import by.vsdev.blealert.domain.Alert
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

/**
 * WebSocket-backed [NotificationService]. Every host value below needs to match wherever the
 * `:server` module is actually reachable from - it differs per test environment (Android
 * emulator loopback vs. iOS simulator vs. a real device on the LAN/behind a tunnel), so there is
 * deliberately no auto-detection here.
 */
internal class WebSocketNotificationService(
    private val scope: CoroutineScope,
    private val backendUrl: String,
) : NotificationService {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private val _connectionState = MutableStateFlow(RemoteConnectionState.IDLE)
    override val connectionState: StateFlow<RemoteConnectionState> = _connectionState

    private val _alerts = MutableSharedFlow<Alert>(extraBufferCapacity = 16)
    override fun alerts(): Flow<Alert> = _alerts.asSharedFlow()

    private val client = HttpClient(CIO) { install(WebSockets) { pingInterval = 15.seconds } }
    private var session: DefaultClientWebSocketSession? = null
    private var connectionJob: Job? = null

    override suspend fun connect() {
        if (connectionJob?.isActive == true) return
        connectionJob = scope.launch { runConnectionLoop() }
    }

    private suspend fun runConnectionLoop() {
        var backoffMillis = INITIAL_BACKOFF_MILLIS
        while (true) {
            _connectionState.value = RemoteConnectionState.CONNECTING
            runCatching {
                client.webSocket(backendUrl) {
                    session = this
                    _connectionState.value = RemoteConnectionState.CONNECTED
                    backoffMillis = INITIAL_BACKOFF_MILLIS
                    for (frame in incoming) {
                        if (frame is Frame.Text) handleFrame(frame.readText())
                    }
                }
            }
            session = null
            _connectionState.value = RemoteConnectionState.DISCONNECTED
            delay(backoffMillis)
            backoffMillis = (backoffMillis * 2).coerceAtMost(MAX_BACKOFF_MILLIS)
        }
    }

    private fun handleFrame(text: String) {
        val envelope = runCatching { json.decodeFromString<AlertEnvelope>(text) }.getOrNull() ?: return
        if (envelope.type == "alert") _alerts.tryEmit(envelope.payload)
    }

    override suspend fun disconnect() {
        connectionJob?.cancel()
        connectionJob = null
        session?.close()
        session = null
        _connectionState.value = RemoteConnectionState.IDLE
    }

    override fun acknowledge(alertId: String) {
        val current = session ?: return
        scope.launch {
            runCatching {
                current.send(Frame.Text(json.encodeToString(AckMessage(alertId = alertId))))
            }
        }
    }

    private companion object {
        const val INITIAL_BACKOFF_MILLIS = 2_000L
        const val MAX_BACKOFF_MILLIS = 30_000L
    }
}

@Serializable
private data class AlertEnvelope(val type: String, val payload: Alert)

@Serializable
private data class AckMessage(val type: String = "ack", val alertId: String)
