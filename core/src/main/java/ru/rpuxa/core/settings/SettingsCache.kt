package ru.rpuxa.core.settings

import ru.rpuxa.core.ThisDeviceInfo
import java.io.*

object SettingsCache {

    private const val FILE_NAME = "settings"

    fun save(settings: Settings, info: ThisDeviceInfo) {
        ObjectOutputStream(FileOutputStream(FILE_NAME)).use {
            it.writeObject(settings.fields)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun load(settings: Settings, info: ThisDeviceInfo) {
        try {
            ObjectInputStream(FileInputStream(FILE_NAME)).use {
                settings.deserializable(it.readObject() as Array<Any?>)
            }
        } catch (e: FileNotFoundException) {
        }
    }
}