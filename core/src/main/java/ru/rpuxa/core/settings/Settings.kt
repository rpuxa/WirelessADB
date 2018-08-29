package ru.rpuxa.core.settings

interface Settings {
    var deviceName: String
    var adbPath: String

    val fields: Array<Any?>

    fun deserializable(fields: Array<Any?>)
}