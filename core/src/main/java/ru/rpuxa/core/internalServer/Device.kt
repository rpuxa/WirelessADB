package ru.rpuxa.core.internalServer

import java.io.Serializable

/**
 * Data-класс устройства
 *
 * [name] - Имя устройства
 * [isMobile] - Является ли устройство телефоном
 */
data class Device(val id: Long, val name: String, val isMobile: Boolean) : Serializable {

    override fun hashCode(): Int {
        return id.toInt() * 31 + name.hashCode() * 7 + if (isMobile) 1 else 0
    }
}