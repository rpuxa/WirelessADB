package ru.rpuxa.core

import java.io.Serializable

@Deprecated("Deprecated class name, use Device instead")
typealias SerializableDevice = Device

/**
 * Data-класс устройства
 *
 * [name] - Имя устройства
 * [isMobile] - Является ли устройство телефоном
 */
data class Device(val id: Long, val name: String, val isMobile: Boolean) : Serializable