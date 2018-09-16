package ru.rpuxa.desktop.visual

import ru.rpuxa.core.checkName
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import javax.swing.JOptionPane

class NamePicker : FieldPicker("Change name", "Name") {

    init {
        fieldText = DesktopDeviceInfo.name
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
                DesktopDeviceInfo.name = res
                fieldText = DesktopDeviceInfo.name
                SettingsCache.save(DesktopSettings, DesktopDeviceInfo)
            }

            break
        }
    }
}