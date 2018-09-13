package ru.rpuxa.pc

import ru.rpuxa.core.internalServer.DeviceInfo
import java.io.File

object PCDeviceInfo : DeviceInfo() {
    override val filesDir = File("config").path!!
    override val isMobile = false
    override lateinit var adbPath: String
    override lateinit var name: String

    override fun serialize() {
        adbPath = PCSettings.adbPath
        name = PCSettings.deviceName
    }
}