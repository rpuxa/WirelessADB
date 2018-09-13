package ru.rpuxa.core.settings

import ru.rpuxa.core.internalServer.DeviceInfo
import java.io.*

object SettingsCache {

    private const val FILE_NAME = "settings"

    fun save(settings: Settings, info: DeviceInfo) {
        ObjectOutputStream(FileOutputStream(File(info.filesDir, FILE_NAME))).use {
            it.writeObject(settings.fields)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun load(settings: Settings, info: DeviceInfo) {
        try {
            ObjectInputStream(FileInputStream(File(info.filesDir, FILE_NAME))).use {
                settings.deserializable(it.readObject() as Array<Any?>)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}