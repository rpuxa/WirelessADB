package ru.rpuxa.desktop.visual

import ru.rpuxa.core.checkName
import ru.rpuxa.core.internalServer.DeviceInfo
import ru.rpuxa.core.settings.Settings
import ru.rpuxa.core.settings.SettingsCache
import javax.swing.JOptionPane

class NamePicker(private val settings: Settings, private val info: DeviceInfo) : FieldPicker("Change name", "Name") {

    init {
        fieldText = info.name
    }

    override fun onButtonClick() {
        while (true) {
            val res = JOptionPane.showInputDialog(this, "Enter name")

            if (res != null) {
                if (!checkName(res)) {
                    JOptionPane.showMessageDialog(this, "Name must contains only [A-Z, a-z, А-Я, а-я, 0-9, _, -] characters,\n" +
                            " and length must be from 4 to 16")
                    continue
                }
                info.name = res
                fieldText = info.name
                SettingsCache.save(settings, info)
            }

            break
        }
    }
}