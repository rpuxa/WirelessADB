package ru.rpuxa.core.listeners

import ru.rpuxa.core.Message

internal interface DeviceListener {

    fun onGetMessage(message: Message)

    fun onDisconnected()
}
