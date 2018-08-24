package ru.rpuxa.wirelessadb.core

interface DeviceListener {

    fun onGetMessage(message: Message)

    fun onDisconnected()
}
