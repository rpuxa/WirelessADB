package ru.rpuxa.pc.visual

import ru.rpuxa.core.SerializableDevice
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class DeviceListPanel : JPanel() {

    fun updateDevices(devices: Array<SerializableDevice>) : DeviceListPanel {
        devices.forEach(::addDevice)
        return this
    }

    private fun addDevice(device: SerializableDevice) {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        val height = 50
        val type = JLabel(if (device.isMobile) "Android" else "PC")

        type.preferredSize = Dimension(100, height)
        panel.add(type)

        val name = JLabel(device.name)
        name.preferredSize = Dimension(300, height)
        panel.add(name)

        if (device.isMobile) {
            val runAdb = JButton("Run ADB")
            runAdb.preferredSize = Dimension(100, height)
            panel.add(runAdb)
        }

        add(panel)
    }
}