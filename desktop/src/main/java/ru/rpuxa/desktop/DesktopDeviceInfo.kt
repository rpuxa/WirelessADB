package ru.rpuxa.desktop

import ru.rpuxa.core.internalServer.DeviceInfo
import java.io.File

object DesktopDeviceInfo : DeviceInfo() {
    override val filesDir = File("config").path!!
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