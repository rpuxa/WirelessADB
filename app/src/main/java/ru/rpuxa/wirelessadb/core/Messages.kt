package ru.rpuxa.wirelessadb.core

import java.io.Serializable

const val ID = 0
const val NAME = 1
const val TYPE = 2


const val CHECK = 4
const val GET_DEVICE_LIST = 5
const val CLOSE_CORE_SERVER = 6
const val EMPTY_MESSAGE = 7

class Message(val command: Int, val data: Any?) : Serializable