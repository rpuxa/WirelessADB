package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.equalsElements
import ru.rpuxa.core.listeners.AdbListener
import ru.rpuxa.pc.Actions
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

const val DISCONNECT = "Disconnect Adb"
const val CONNECT = "Connect Adb"

class DeviceListPanel(actions: Actions) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private var lastDevices: Array<Device>? = null

    fun updateDevices(devices: Array<Device>): DeviceListPanel {
        if (lastDevices != null && devices.equalsElements(lastDevices!!))
            return this
        lastDevices = devices
        devices.forEach(::addDevice)
        revalidate()
        return this
    }

    private fun addDevice(device: Device) {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        val type = JLabel(if (device.isMobile) "Android" else "PC")

        panel.add(type)
        panel.add(Box.createHorizontalStrut(70 - type.minimumSize.width), BorderLayout.WEST)

        val name = JLabel(device.name)
        panel.add(name)
        panel.add(Box.createHorizontalStrut(200 - type.minimumSize.width))


        if (device.isMobile)
            Thread {
                var connected = CoreServer.checkAdb(device)
                val runAdb = JButton(if (connected) DISCONNECT else CONNECT)
                runAdb.addActionListener {
                    connected = CoreServer.checkAdb(device)
                    if (connected) {
                        CoreServer.disconnectAdb(device)
                        connected = false
                        runAdb.text = CONNECT
                    } else {
                        CoreServer.connectAdb(device, object : AdbListener {
                            override fun onConnect() {
                                runAdb.text = DISCONNECT
                                connected = true
                            }

                            override fun onDisconnect() {
                                runAdb.text = CONNECT
                                connected = false
                            }
                        })
                    }
                }
                panel.add(runAdb)
                revalidate()
            }.start()


        panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.maximumSize = Dimension(500, 30)
        add(panel, BorderLayout.WEST)

    }
}