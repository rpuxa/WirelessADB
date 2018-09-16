package ru.rpuxa.desktop

import ru.rpuxa.core.settings.Settings

object DesktopSettings : Settings {
    override var deviceName = "Computer"

    override var adbPath = "C:\\"

    override var autoConnectIds: MutableSet<Long> = HashSet()

    override var autoStart = false

    override val fields: Array<Any?>
        get() = arrayOf(
                deviceName,
                adbPath,
                autoConnectIds,
                autoStart
        )

    override fun deserializable(fields: Array<Any?>) {
        deviceName = fields[0] as String
        adbPath = fields[1] as String
        autoConnectIds = fields[2] as MutableSet<Long>
        autoStart = fields[3] as Boolean
    }
}