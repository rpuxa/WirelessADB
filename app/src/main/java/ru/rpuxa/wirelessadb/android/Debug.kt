package ru.rpuxa.wirelessadb.android

import ru.rpuxa.wirelessadb.MainActivity
import ru.rpuxa.wirelessadb.core.CoreServer

/**
 * Класс тупо для дебага
 */
object Debug {

    fun debug(androidDeviceInfo: MainActivity.AndroidDeviceInfo) {
        Thread {
            CoreServer.startServer(androidDeviceInfo)
        }.start()
    }
}