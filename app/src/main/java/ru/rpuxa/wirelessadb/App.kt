package ru.rpuxa.wirelessadb

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
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
        override val isWifiEnable: Boolean
            get() {
                val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                return mWifi.isConnected
            }
    }
}