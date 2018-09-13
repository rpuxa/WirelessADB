package ru.rpuxa.internalServer

import ru.rpuxa.core.internalServer.Message

internal interface DeviceListener {

    fun onGetMessage(message: Message)

    fun onDisconnected()
}
