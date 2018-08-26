package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.SerializableDevice
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import java.awt.Dimension
import javax.swing.*

class MainPanel(val actions: Actions) : JPanel() {
    val mainSwitch = JCheckBox("Enable Wireless Adb")
    val autoLoading = JCheckBox("Add service to auto-loading")
    val deviceList = JList<SerializableDevice>()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //first line
        add(mainSwitch)

        //second Line
        add(autoLoading)

        //third line
        add(deviceList)

        deviceList.add(JButton("sdfskldfj"))

    }

    fun setDevices(list: Array<SerializableDevice>) {
        list.forEach {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
            val height = 50
            val type = JLabel(
                    if (it.isMobile) "Android" else "PC"
            )

            type.preferredSize = Dimension(100, height)
            panel.add(type)

            val name = JLabel(it.name)
            name.preferredSize = Dimension(300, height)
            panel.add(name)

            if (it.isMobile) {
                val runAdb = JButton("Run ADB")
                runAdb.preferredSize = Dimension(100, height)
                panel.add(runAdb)
            }

            deviceList.add(panel)
        }
        if (list.isNotEmpty())
            println("Установили девайсы")
    }

    init {
        Thread {
            while (true) {
                val available = CoreServer.isAvailable
                mainSwitch.isSelected = available
                if (available) {
                    val devices = CoreServer.getDevicesList()
                    if (devices != null) {
                        setDevices(devices)
                    }
                }
                Thread.sleep(3000)
            }
        }.start()

        mainSwitch.addActionListener {
            if (!CoreServer.isAvailable) {
                CoreServer.startServer(PCDeviceInfo)
            } else {
                CoreServer.closeServer()
            }
        }
    }
}