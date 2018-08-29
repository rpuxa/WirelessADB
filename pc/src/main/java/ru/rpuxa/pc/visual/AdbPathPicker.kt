package ru.rpuxa.pc.visual

import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCSettings
import ru.rpuxa.pc.containsAdb
import javax.swing.*


class AdbPathPicker(actions: Actions) : JPanel() {
    private val pathLabel = JLabel()
    private val selectButton = JButton("Select path")

    init {
        layout = BoxLayout(this, BoxLayout.X_AXIS)

        pathLabel.text = "Path: ${PCSettings.adbPath}     "
        add(pathLabel)

        selectButton.addActionListener { _ ->
            val chooser = JFileChooser()
            chooser.dialogTitle = "Select adb folder"
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val result = chooser.showOpenDialog(this@AdbPathPicker)
            if (result == JFileChooser.APPROVE_OPTION) {
                if (chooser.selectedFile.containsAdb) {
                    PCSettings.adbPath = chooser.selectedFile.toString()
                } else {
                    actions.sendMessage("This folder doesn't contain adb.exe", this)
                }
            }
        }
        add(selectButton)
    }


}