package ru.rpuxa.core

import java.io.Serializable

internal class Message(internal val command: Int, internal val data: Any?) : Serializable {
    companion object {
        const val serialVersionUID = -74529411667180173L
    }
}