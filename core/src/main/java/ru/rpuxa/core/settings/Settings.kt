package ru.rpuxa.core.settings

interface Settings {
    var deviceName: String
    var adbPath: String
    var autoConnectIds: MutableSet<Long>


    val fields: Array<Any?>

    fun deserializable(fields: Array<Any?>)
}