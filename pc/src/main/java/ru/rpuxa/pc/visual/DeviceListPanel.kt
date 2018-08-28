package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.SerializableDevice
import ru.rpuxa.pc.Actions
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class DeviceListPanel(actions: Actions) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    fun updateDevices(devices: Array<SerializableDevice>) : DeviceListPanel {
        devices.forEach(::addDevice)
        return this
    }

    private fun addDevice(device: SerializableDevice) {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        val type = JLabel(if (device.isMobile) "Android" else "PC")

        panel.add(type)
        panel.add(Box.createHorizontalStrut(70 - type.minimumSize.width), BorderLayout.WEST)

        val name = JLabel(device.name)
        panel.add(name)
        panel.add(Box.createHorizontalStrut(200 - type.minimumSize.width))


        if (device.isMobile) {
            val runAdb = JButton("Connect ADB")
            runAdb.addActionListener {
                if (CoreServer.connectAdb(device)) {
                    runAdb.text = "Disconnect ADB"
                }
            }
            panel.add(runAdb)
        }


        panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.maximumSize = Dimension(400, 30)
        add(panel, BorderLayout.WEST)

    }
}