package ru.rpuxa.pc.visual

import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.pc.Actions
import ru.rpuxa.pc.PCDeviceInfo
import ru.rpuxa.pc.PCSettings
import java.awt.Component
import java.awt.Dimension
import java.io.File
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

    private val listener = Listener()

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

    //Установка листенеров
    init {
        InternalServerController.setListener(listener)

        mainSwitch.addActionListener {
            if (!InternalServerController.isAvailable) {
                InternalServerController.startServer(PCDeviceInfo, ServerStarter)
            } else {
                deviceListPanel.clear()
                InternalServerController.closeServer()
            }
        }
    }


    inner class Listener : InternalServerController.InternalServerListener {
        override fun onServerConnected() {
            mainSwitch.isSelected = true
            InternalServerController.setDeviceInfo(PCDeviceInfo)
        }

        override fun onServerDisconnect() {
            mainSwitch.isSelected = false
        }

        override fun serverStillWorking() {
            mainSwitch.isSelected = true
        }

        override fun serverStillDisabled() {
            mainSwitch.isSelected = false
        }

        override fun onDeviceDetected(device: Device) {
            deviceListPanel.addDevice(device)
        }

        override fun onDeviceDisconnected(device: Device) {
            deviceListPanel.removeDevice(device)
        }

        override fun onAdbConnected(device: Device) {
            deviceListPanel.changeAdb(device, true)
        }

        override fun onAdbDisconnected(device: Device) {
            deviceListPanel.changeAdb(device, false)
        }

        override fun onAdbError(code: Int) {
        }
    }

    object ServerStarter : InternalServerController.Starter {

        override fun startServer() {
            val file = File("WAJAR.jar")
            val process = Runtime.getRuntime().exec("java -jar $file server")
            process.waitFor()
        }
    }
}