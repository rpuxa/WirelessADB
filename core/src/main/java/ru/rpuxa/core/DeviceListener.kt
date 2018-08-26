package ru.rpuxa.core

internal interface DeviceListener {

    fun onGetMessage(message: Message)

    fun onDisconnected()
}
