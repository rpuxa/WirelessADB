package ru.rpuxa.wirelessadb

import android.app.Application
import ru.rpuxa.core.internalServer.DeviceInfo
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.core.trd
import ru.rpuxa.internalServer.InternalServer
import ru.rpuxa.wirelessadb.settings.AndroidSettings

class WirelessAdb : Application() {

    companion object {
        internal lateinit var deviceInfo: DeviceInfo

        internal lateinit var serverStarter: InternalServerController.Starter
    }

    override fun onCreate() {
        super.onCreate()
        deviceInfo = AndroidDeviceInfo()
        serverStarter = ServerStarter()
        SettingsCache.load(AndroidSettings, deviceInfo)
    }

    override fun onTerminate() {
        super.onTerminate()
        SettingsCache.save(AndroidSettings, deviceInfo)
    }

    inner class AndroidDeviceInfo : DeviceInfo() {
        override val filesDir = this@WirelessAdb.filesDir.toString()
        override val isMobile = true
        override var adbPath = ""
        override var name
            get() = AndroidSettings.deviceName
            set(value) {
                AndroidSettings.deviceName = value
            }
    }

    inner class ServerStarter : InternalServerController.Starter {
        override fun startServer() = trd {
            InternalServer.init()
        }
    }
}