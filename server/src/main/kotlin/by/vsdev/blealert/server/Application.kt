package by.vsdev.blealert.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.seconds

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

fun main() {
    val botToken = System.getenv("TELEGRAM_BOT_TOKEN")
        ?: error("TELEGRAM_BOT_TOKEN environment variable is required")
    val chatId = System.getenv("TELEGRAM_CHAT_ID")
        ?: error("TELEGRAM_CHAT_ID environment variable is required")

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val telegram = TelegramNotifier(botToken, chatId)
    val broadcaster = AlertBroadcaster(scope, ackTimeout = 8.seconds, telegram = telegram)
    val simulator = AlertSimulator(scope, broadcaster)
    simulator.start()

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module(broadcaster, simulator)
    }.start(wait = true)
}

fun Application.module(broadcaster: AlertBroadcaster, simulator: AlertSimulator) {
    install(WebSockets)

    routing {
        webSocket("/ws/alerts") {
            broadcaster.register(this)
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val ack = runCatching { json.decodeFromString<AckMessage>(frame.readText()) }.getOrNull()
                        if (ack?.type == "ack") broadcaster.onAck(ack.alertId)
                    }
                }
            } finally {
                broadcaster.unregister(this)
            }
        }

        post("/simulate/{scenario}") {
            val alert = simulator.scenarioByKey(call.parameters["scenario"])
            if (alert == null) {
                call.respond(HttpStatusCode.NotFound, "Unknown scenario")
                return@post
            }
            broadcaster.broadcast(alert)
            call.respondText("OK")
        }
    }
}

@Serializable
private data class AckMessage(val type: String, val alertId: String)
