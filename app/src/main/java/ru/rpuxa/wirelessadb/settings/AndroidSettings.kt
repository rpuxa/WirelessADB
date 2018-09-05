package ru.rpuxa.wirelessadb.settings

import android.content.Context
import ru.rpuxa.core.Device
import ru.rpuxa.core.settings.Settings
import java.util.*
import kotlin.collections.HashSet

object AndroidSettings : Settings {
    override var deviceName = "Android"

    var language = Languages.ENGLISH
    var autoStart = true

    override var autoConnectIds: MutableSet<Long> = HashSet()


    fun isAutoConnect(device: Device) = autoConnectIds.find { it == device.id } != null

    fun setLanguage(language: Languages, context: Context) {
        if (this.language == language)
            return
        val resources = context.resources
        val conf = resources.configuration
        conf.setLocale(Locale(language.languageName))
        context.createConfigurationContext(conf)

        this.language = language
    }

    override var adbPath: String
        get() = throw UnsupportedOperationException()
        set(_) = throw UnsupportedOperationException()

    override val fields: Array<Any?>
        get() = arrayOf(
                deviceName,
                language,
                autoConnectIds
        )

    override fun deserializable(fields: Array<Any?>) {
        deviceName = fields[0] as String
        language = fields[1] as Languages
        autoConnectIds = fields[2] as MutableSet<Long>
    }
}