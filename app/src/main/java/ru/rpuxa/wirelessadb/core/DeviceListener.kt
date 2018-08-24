package ru.rpuxa.wirelessadb.core

internal interface DeviceListener {

    fun onGetMessage(message: Message)

    fun onDisconnected()
}
