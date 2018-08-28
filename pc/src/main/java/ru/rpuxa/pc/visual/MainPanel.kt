package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.ServerListener
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class MainPanel(val actions: Actions) : JPanel() {
    private val mainSwitch = JCheckBox("Enable Wireless Adb")
    private val autoLoading = JCheckBox("Add service to auto-loading")
    private val devicesLabel = JLabel("Devices:")

    private val deviceListPanel = DeviceListPanel(actions)
    private val deviceArray = ArrayList<Device>()

    //Размещение компонентов
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //first line
        mainSwitch.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        //second Line
        devicesLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        //third line
        add(devicesLabel)

        //forth line
        val scroll = JScrollPane(deviceListPanel)
        scroll.maximumSize = Dimension(850, 200)

        add(scroll)
    }

    //Установка листенеров и запуск потоков
    init {
        Thread {
            while (true) {
                val available = CoreServer.isAvailable
                mainSwitch.isSelected = available
                Thread.sleep(3000)
            }
        }.start()

        mainSwitch.addActionListener { _ ->
            if (!CoreServer.isAvailable) {
                CoreServer.startServer(PCDeviceInfo, object : ServerListener {
                    override fun onAdd(device: Device) {
                        deviceArray.add(device)
                        updateDevices()
                    }

                    override fun onRemove(device: Device) {
                        deviceArray.removeIf { it.id == device.id }
                        updateDevices()
                    }
                })
            } else {
                CoreServer.closeServer()
            }
        }
    }

    private fun updateDevices() {
        deviceListPanel.updateDevices(deviceArray.toTypedArray())
    }
}