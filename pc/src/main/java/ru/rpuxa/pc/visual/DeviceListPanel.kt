package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.AdbListener
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

const val DISCONNECT = "Disconnect Adb"
const val DISCONNECTING = "Disconnecting..."
const val CONNECT = "Connect Adb"
const val CONNECTING = "Connecting..."

class DeviceListPanel : JPanel() {

    private val runAdbButtons = ArrayList<Pair<Device, JButton>>()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    fun updateDevices(devices: Array<Device>): DeviceListPanel {
        removeAll()
        runAdbButtons.clear()
        devices.forEach(::addDevice)
        repaint()
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
                val runAdb = JButton()
                val adbListener = object : AdbListener {
                    override fun onConnect() {
                        runAdb.text = DISCONNECT
                    }

                    override fun onDisconnect() {
                        runAdb.text = CONNECT
                    }
                }
                if (CoreServer.checkAdb(device)) {
                    adbListener.onConnect()
                } else {
                    adbListener.onDisconnect()
                }
                runAdb.addActionListener {
                    if (CoreServer.checkAdb(device)) {
                        runAdb.text = DISCONNECTING
                        CoreServer.disconnectAdb(device, adbListener)
                    } else {
                        runAdb.text = CONNECTING
                        CoreServer.connectAdb(device, adbListener)
                    }
                }
                panel.add(runAdb)
                revalidate()
                runAdbButtons.add(device to runAdb)
            }.start()


        panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.maximumSize = Dimension(500, 30)
        add(panel)

    }

    fun refresh() {
        for ((device, button) in runAdbButtons.toTypedArray())
            button.text = if (CoreServer.checkAdb(device)) DISCONNECT else CONNECT


    }
}