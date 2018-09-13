package ru.rpuxa.pc

import ru.rpuxa.core.internalServer.DeviceInfo
import java.io.File

object PCDeviceInfo : DeviceInfo() {
    override val filesDir = File("config").path!!
    override val isMobile = false
    override var adbPath: String
        get() = PCSettings.adbPath
        set(value) {
            PCSettings.adbPath = value
        }
    override var name: String
        get() = PCSettings.deviceName
        set(value) {
            PCSettings.deviceName = value
        }
}