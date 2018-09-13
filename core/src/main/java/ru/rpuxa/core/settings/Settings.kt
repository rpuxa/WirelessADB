package ru.rpuxa.core.settings

interface Settings {
    var deviceName: String
    var autoConnectIds: MutableSet<Long>
    var autoStart: Boolean
    var adbPath: String




    val fields: Array<Any?>

    fun deserializable(fields: Array<Any?>)
}