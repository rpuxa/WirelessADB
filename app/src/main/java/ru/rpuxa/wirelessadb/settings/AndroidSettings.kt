package ru.rpuxa.wirelessadb.settings

import ru.rpuxa.core.settings.Settings

object AndroidSettings : Settings {
    override var deviceName = "Android"

    var language = Languages.ENGLISH
        set(value) {
            if (field == value)
                return

            //TODO Сделать смену языка
            when (value) {
                Languages.ENGLISH -> TODO("Ангийский язык")
                Languages.RUSSIAN -> TODO("Русский")
            }

            field = value
        }

    override var adbPath: String
        get() = throw UnsupportedOperationException()
        set(_) = throw UnsupportedOperationException()

    override val fields: Array<Any?>
        get() = arrayOf(
                deviceName,
                language
        )

    override fun deserializable(fields: Array<Any?>) {
        deviceName = fields[0] as String
        language = fields[1] as Languages
    }
}