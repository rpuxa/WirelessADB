package ru.rpuxa.wirelessadb

import android.app.Application
import ru.rpuxa.core.ThisDeviceInfo
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.wirelessadb.settings.AndroidSettings

internal lateinit var ANDROID_DEVICE_INFO: App.AndroidDeviceInfo

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ANDROID_DEVICE_INFO = AndroidDeviceInfo()
        SettingsCache.load(AndroidSettings, ANDROID_DEVICE_INFO)
    }

    override fun onTerminate() {
        super.onTerminate()
        SettingsCache.save(AndroidSettings, ANDROID_DEVICE_INFO)
    }

    inner class AndroidDeviceInfo : ThisDeviceInfo() {
        override val settings = AndroidSettings
        override val filesDir = this@App.filesDir!!
        override val isMobile = true
    }
}