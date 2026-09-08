package by.vsdev.blealert.server

import by.vsdev.blealert.domain.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * Fallback delivery path used when the WebSocket ACK for an [Alert] doesn't arrive in time.
 * Uses the JDK's built-in HTTP client - a single REST call doesn't justify pulling in a whole
 * extra HTTP library.
 */
class TelegramNotifier(private val botToken: String, private val chatId: String) {

    private val http = HttpClient.newHttpClient()

    suspend fun sendFallback(alert: Alert) {
        val text = "⚠️ ${alert.type} (${alert.severity}) " +
            "— доставка через " +
            "сокет не подтверждена"
        val escapedText = text.replace("\"", "\\\"")
        val body = """{"chat_id":"$chatId","text":"$escapedText"}"""
        val request = HttpRequest.newBuilder(URI("https://api.telegram.org/bot$botToken/sendMessage"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()
        runCatching {
            withContext(Dispatchers.IO) { http.send(request, HttpResponse.BodyHandlers.ofString()) }
        }.onSuccess { response ->
            println("Telegram fallback sent for alert ${alert.id}, status=${response.statusCode()}")
        }.onFailure { cause ->
            System.err.println("Telegram fallback failed for alert ${alert.id}: ${cause.message}")
        }
    }
}
