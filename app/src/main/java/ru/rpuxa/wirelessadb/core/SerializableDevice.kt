package ru.rpuxa.wirelessadb.core

import java.io.Serializable


/**
 * Data-класс устройства
 *
 * [name] - Имя устройства
 * [isMobile] - Является ли устройство телефоном
 */
data class SerializableDevice(internal val id: Long, val name: String, val isMobile: Boolean) : Serializable