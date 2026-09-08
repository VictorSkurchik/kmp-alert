package by.vsdev.blealert

import android.app.Application
import by.vsdev.blealert.di.initKoin
import org.koin.android.ext.koin.androidContext

class BleAlertApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@BleAlertApplication) }
    }
}
