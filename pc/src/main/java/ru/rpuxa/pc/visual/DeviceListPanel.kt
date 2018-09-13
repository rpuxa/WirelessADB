package ru.rpuxa.pc.visual

import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

const val DISCONNECT = "Disconnect Adb"
const val DISCONNECTING = "Disconnecting..."
const val CONNECT = "Connect Adb"
const val CONNECTING = "Connecting..."

class DeviceListPanel : JPanel() {

    private val deviceViews = ArrayList<DeviceView>()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    fun addDevice(device: Device) {
        drawDevice(device)
        revalidate()
    }

    fun removeDevice(device: Device) {
        remove(deviceViews.find { it.device == device }!!.panel)
        deviceViews.removeIf { it.device == device }
        revalidate()
    }

    fun changeAdb(device: Device, connected: Boolean) {
        deviceViews.find { it.device == device }!!.adbButton!!.text = if (connected) CONNECT else DISCONNECT
    }

    private fun drawDevice(device: Device) {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
        val type = JLabel(if (device.isMobile) "Android" else "PC")

        panel.add(type)
        panel.add(Box.createHorizontalStrut(70 - type.minimumSize.width), BorderLayout.WEST)

        val name = JLabel(device.name)
        panel.add(name)
        panel.add(Box.createHorizontalStrut(200 - type.minimumSize.width))

        var adbButton: JButton? = null

        if (device.isMobile) {
            adbButton = JButton(CONNECT)
            adbButton.addActionListener {
                if (InternalServerController.checkAdb(device)) {
                    adbButton.text = DISCONNECTING
                    InternalServerController.disconnectAdb(device)
                } else {
                    adbButton.text = CONNECTING
                    InternalServerController.connectAdb(device)
                }
            }
            panel.add(adbButton)
        }


        panel.alignmentX = Component.LEFT_ALIGNMENT
        panel.maximumSize = Dimension(500, 30)

        deviceViews.add(DeviceView(device, panel, adbButton))

        add(panel)
    }

    fun clear() {
        removeAll()
        deviceViews.clear()
        revalidate()
    }


    class DeviceView(val device: Device, val panel: JPanel, val adbButton: JButton?)
}