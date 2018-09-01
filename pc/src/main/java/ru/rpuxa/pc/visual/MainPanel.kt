package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.Device
import ru.rpuxa.core.listeners.ServerListener
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import ru.rpuxa.pc.PCSettings
import java.awt.Component
import java.awt.Dimension
import javax.swing.*

class MainPanel(actions: Actions) : JPanel() {
    init {
        SettingsCache.load(PCSettings, PCDeviceInfo)
    }

    private val mainSwitch = JCheckBox("Enable Wireless Adb")
    private val autoLoading = JCheckBox("Add service to auto-loading")
    private val devicesLabel = JLabel("Devices:")

    private val adbPathPicker = AdbPathPicker(actions)
    private val deviceListPanel = DeviceListPanel()

    private val deviceList = ArrayList<Device>()

    //Размещение компонентов
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //first line
        devicesLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        //second Line
        devicesLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        //third line
        adbPathPicker.alignmentX = Component.LEFT_ALIGNMENT
        add(adbPathPicker)

        //forth line
        devicesLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(devicesLabel)


        val scroll = JScrollPane(deviceListPanel)
        scroll.maximumSize = Dimension(850, 200)//850 200
        scroll.alignmentX = Component.LEFT_ALIGNMENT
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
                        deviceList.add(device)
                        updateDevices()
                    }

                    override fun onRemove(device: Device) {
                        if (deviceList.isNotEmpty()) {
                            deviceList.removeIf { it.id == device.id }
                            updateDevices()
                        }
                    }
                })
            } else {
                deviceList.clear()
                updateDevices()
                CoreServer.closeServer()
            }
        }
    }

    private fun updateDevices() {
        deviceListPanel.updateDevices(deviceList.toTypedArray())
    }
}