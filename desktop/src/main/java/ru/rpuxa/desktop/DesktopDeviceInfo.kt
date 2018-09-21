package ru.rpuxa.desktop

import ru.rpuxa.core.internalServer.DeviceInfo

object DesktopDeviceInfo : DeviceInfo() {
    override val filesDir by lazy {
        System.getProperty("user.home") + "\\AppData\\Roaming\\WirelessAdb"
    }
    override val isMobile = false
    override var adbPath: String
        get() = DesktopSettings.adbPath
        set(value) {
            DesktopSettings.adbPath = value
        }
    override var name: String
        get() = DesktopSettings.deviceName
        set(value) {
            DesktopSettings.deviceName = value
        }


}
