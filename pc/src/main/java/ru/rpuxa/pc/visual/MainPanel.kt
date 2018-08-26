package ru.rpuxa.pc.visual

import ru.rpuxa.core.CoreServer
import ru.rpuxa.core.SerializableDevice
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import javax.swing.*

class MainPanel(val actions: Actions) : JPanel() {
    private val mainSwitch = JCheckBox("Enable Wireless Adb")
    private val autoLoading = JCheckBox("Add service to auto-loading")
    private val deviceList = JList<SerializableDevice>()

    //Размещение компонентов
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        //first line
        add(mainSwitch)

        //second Line
        add(autoLoading)

        //third line
        add(deviceList)

        add(JScrollPane(DeviceListPanel().updateDevices(arrayOf(
            SerializableDevice(1, "asdasd", true),
            SerializableDevice(1, "asdasd", false)
        ))))
    }

    //Установка листенеров и запуск потоков
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