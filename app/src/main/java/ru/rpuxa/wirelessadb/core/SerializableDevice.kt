package ru.rpuxa.wirelessadb.core

import java.io.Serializable

class SerializableDevice(val id: Long, val name: String, val isMobile: Boolean) : Serializable