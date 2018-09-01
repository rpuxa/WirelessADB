package ru.rpuxa.core.listeners

interface AdbListener {

    /**
     * Вызывается при успешном подключении adb
     */
    fun onConnect()

    /**
     * Вызывается при разрыве соединения.
     */
    fun onDisconnect()

    /**
     * Вызывается при ошибке подключения адб
     * [code] - код ошибки
     */
    fun onError(code: Int) {
    }
}