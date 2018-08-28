package ru.rpuxa.core.listeners

import ru.rpuxa.core.Device

interface ServerListener {

    /**
     * Вызывается при обнаружении н
     */
    fun onAdd(device: Device)

    fun onRemove(device: Device)
}