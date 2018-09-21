package ru.rpuxa.desktop.visual

import ru.rpuxa.core.containsAdb
import ru.rpuxa.core.internalServer.DeviceInfo
import ru.rpuxa.core.settings.Settings
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.desktop.Actions
import javax.swing.JFileChooser


class AdbPathPicker(private val settings: Settings,
                    private val info: DeviceInfo,
                    private val actions: Actions) : FieldPicker("Select path", "Adb path") {

    init {
        fieldText = settings.adbPath
    }

    override fun onButtonClick() {
        val chooser = JFileChooser()
        chooser.dialogTitle = "Select adb folder"
        chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val result = chooser.showOpenDialog(this@AdbPathPicker)
        if (result == JFileChooser.APPROVE_OPTION) {
            if (chooser.selectedFile.containsAdb) {
                fieldText = chooser.selectedFile.toString()
                settings.adbPath = chooser.selectedFile.toString()
                SettingsCache.save(settings, info)
            } else {
                actions.sendMessage("This folder doesn't contain adb.exe", this)
            }
        }
    }
}