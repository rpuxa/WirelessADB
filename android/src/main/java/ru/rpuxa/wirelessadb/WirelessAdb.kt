package ru.rpuxa.wirelessadb

import android.app.Application
import android.content.Intent
import ru.rpuxa.core.internalServer.DeviceInfo
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class WirelessAdb : Application() {

    companion object {
        internal lateinit var deviceInfo: DeviceInfo

        internal lateinit var serverStarter: InternalServerController.Starter
    }

    override fun onCreate() {
        super.onCreate()
        init()
        SettingsCache.load(AndroidSettings, deviceInfo)
    }

    override fun onTerminate() {
        super.onTerminate()
        SettingsCache.save(AndroidSettings, deviceInfo)
    }

    private fun init() {
        deviceInfo = object : DeviceInfo() {
            override val filesDir = this@WirelessAdb.filesDir.toString()
            override val isMobile = true
            override var adbPath = ""
            override var name
            get() = AndroidSettings.deviceName
            set(value) {
                AndroidSettings.deviceName = value
            }
        }

        serverStarter = object : InternalServerController.Starter {
            override fun startServer() {
                startService(Intent(this@WirelessAdb, ServerStarterService::class.java))
            }
        }
    }
}