package ru.rpuxa.core.settings

interface Settings {
    var deviceName: String
    var adbPath: String
    var autoConnectIds: MutableSet<Long>
    var autoStart: Boolean



    val fields: Array<Any?>

    fun deserializable(fields: Array<Any?>)
}