package by.vsdev.blealert.di

import by.vsdev.blealert.data.dataModule
import by.vsdev.blealert.data.platformAlertModule
import by.vsdev.blealert.feature.dashboard.dashboardModule
import by.vsdev.blealert.feature.monitoring.monitoringModule
import by.vsdev.blealert.feature.settings.settingsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(dataModule, platformAlertModule, dashboardModule, monitoringModule, settingsModule)
    }
}
