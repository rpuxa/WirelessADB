package ru.rpuxa.core

import org.jetbrains.annotations.NotNull
import ru.rpuxa.core.settings.Settings
import java.io.*
import java.util.*

abstract class ThisDeviceInfo {
    abstract val settings: Settings
    abstract val filesDir: File
    abstract val isMobile: Boolean
    abstract val isWifiEnable: Boolean


    @NotNull
    open val id: Long? = null
        get() = try {
            field ?: ObjectInputStream(FileInputStream(File(filesDir, "id"))).use {
                it.readObject() as Long
            }
        } catch (e: Exception) {
            val id = Random().nextLong()
            ObjectOutputStream(FileOutputStream(File(filesDir, "id"))).use {
                it.writeObject(id)
            }
            id
        }
}
