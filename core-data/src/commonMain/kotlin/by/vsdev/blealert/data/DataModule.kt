package by.vsdev.blealert.data

import by.vsdev.blealert.domain.AlertRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val dataModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    single<NotificationService> { WebSocketNotificationService(get(), backendUrl) }
    single<AlertRepository> { AlertRepositoryImpl(get(), get(), get()) }
}
