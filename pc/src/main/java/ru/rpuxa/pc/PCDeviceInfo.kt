package ru.rpuxa.pc

import ru.rpuxa.core.ThisDeviceInfo
import java.io.File

object PCDeviceInfo : ThisDeviceInfo() {
    override val baseName = "Personal Computer"
    override val filesDir = File("config")
    override val isMobile = false
}