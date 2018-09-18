package ru.rpuxa.desktop.visual

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
        val deviceView = deviceViews.find { it.device == device }
        if (deviceView != null) {
            val panel = deviceView.panel
            remove(panel)
            deviceViews.removeIf { it.device == device }
            repaint()
        }
    }

    fun changeAdb(device: Device, connected: Boolean) {
        deviceViews.find { it.device == device }!!.adbButton!!.text = if (connected) DISCONNECT else CONNECT
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
        repaint()
    }


    class DeviceView(val device: Device, val panel: JPanel, val adbButton: JButton?)
}