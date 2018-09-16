package ru.rpuxa.desktop.visual

import ru.rpuxa.core.containsAdb
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.desktop.Actions
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import javax.swing.JFileChooser


class AdbPathPicker(private val actions: Actions) : FieldPicker("Select path", "Adb path") {

    init {
        fieldText = DesktopSettings.adbPath
    }

    override fun onButtonClick() {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Select adb folder"
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = chooser.showOpenDialog(this@AdbPathPicker)
        if (result == JFileChooser.APPROVE_OPTION) {
            if (chooser.selectedFile.containsAdb) {
                fieldText = chooser.selectedFile.toString()
                DesktopSettings.adbPath = chooser.selectedFile.toString()
                SettingsCache.save(DesktopSettings, DesktopDeviceInfo)
            } else {
                actions.sendMessage("This folder doesn't contain adb.exe", this)
            }
        }
    }
}