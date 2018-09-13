package ru.rpuxa.core.internalServer

import java.io.Serializable

class Message(val command: Int, val data: Any? = null) : Serializable {
    companion object {
        const val serialVersionUID = -74529411667180173L
    }
}