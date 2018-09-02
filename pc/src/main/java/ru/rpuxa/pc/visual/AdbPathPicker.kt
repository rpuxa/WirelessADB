package ru.rpuxa.pc.visual

import ru.rpuxa.core.containsAdb
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import ru.rpuxa.pc.PCSettings
import javax.swing.*


class AdbPathPicker(actions: Actions) : JPanel() {
    private val pathLabel = JLabel()
    private val selectButton = JButton("Select path")

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        setPath(PCSettings.adbPath)
        add(pathLabel)

        selectButton.addActionListener { _ ->
            val chooser = JFileChooser()
            chooser.dialogTitle = "Select adb folder"
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val result = chooser.showOpenDialog(this@AdbPathPicker)
            if (result == JFileChooser.APPROVE_OPTION) {
                if (chooser.selectedFile.containsAdb) {
                    setPath(chooser.selectedFile.toString())
                    PCSettings.adbPath = chooser.selectedFile.toString()
                    SettingsCache.save(PCSettings, PCDeviceInfo)
                } else {
                    actions.sendMessage("This folder doesn't contain adb.exe", this)
                }
            }
        }
        add(selectButton)
    }

    private fun setPath(path: String) {
        pathLabel.text = "ADB Path: $path     "
    }
}