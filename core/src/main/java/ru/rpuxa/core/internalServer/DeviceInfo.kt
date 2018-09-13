package ru.rpuxa.core.internalServer

import java.io.*
import java.util.*

abstract class DeviceInfo {
    abstract val filesDir: String
    abstract val isMobile: Boolean
    abstract var adbPath: String
    abstract var name: String

    @Transient
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
