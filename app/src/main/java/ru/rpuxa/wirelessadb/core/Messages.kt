package ru.rpuxa.wirelessadb.core

import java.io.Serializable

internal const val ID = 0
internal const val NAME = 1
internal const val TYPE = 2


internal const val CHECK = 4
internal const val GET_DEVICE_LIST = 5
internal const val CLOSE_CORE_SERVER = 6
internal const val EMPTY_MESSAGE = 7

internal class Message(internal val command: Int, internal val data: Any?) : Serializable