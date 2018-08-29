package ru.rpuxa.pc

import ru.rpuxa.core.ThisDeviceInfo
import java.io.File

object PCDeviceInfo : ThisDeviceInfo() {
    override val settings = PCSettings
    override val filesDir = File("config")
    override val isMobile = false
}