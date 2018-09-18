package ru.rpuxa.desktop.visual

import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.desktop.Actions
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import ru.rpuxa.desktop.DesktopUtils
import java.awt.Component
import java.awt.Dimension
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.*

class MainPanel(actions: Actions, showAdbPathRow: Boolean = true) : JPanel() {
    init {
        SettingsCache.load(DesktopSettings, DesktopDeviceInfo)
        SettingsCache.save(DesktopSettings, DesktopDeviceInfo)
    }

    private val mainSwitch = JCheckBox("Enable Wireless Adb")
    private val autoLoading = JCheckBox("Add service to auto-loading")
    private val devicesLabel = JLabel("Type      Name")

    private val adbPathPicker = AdbPathPicker(actions)
    private val namePicker = NamePicker()
    private val deviceListPanel = DeviceListPanel()

    private val listener = object : InternalServerController.InternalServerListener {
        override fun onServerConnected() {
            mainSwitch.isSelected = true
            InternalServerController.setDeviceInfo(DesktopDeviceInfo)
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

        override fun onAdbError(device: Device, code: Int) {
        }
    }

    //Размещение компонентов
    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        mainSwitch.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        autoLoading.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        if (showAdbPathRow) {
            adbPathPicker.alignmentX = Component.LEFT_ALIGNMENT
            add(adbPathPicker)
        }

        namePicker.alignmentX = Component.LEFT_ALIGNMENT
        add(namePicker)

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
                InternalServerController.startServer(DesktopDeviceInfo, ServerStarter)
            } else {
                deviceListPanel.clear()
                InternalServerController.closeServer()
            }
        }
    }




    object ServerStarter : InternalServerController.Starter {

        private const val SERVER = "jars\\internalServer.jar"

        override fun startServer() {
            val absPath = DesktopUtils.getResource(SERVER).path

            val dividerIndex = absPath.indexOf("%5c")
            val path = absPath.substring(0, dividerIndex)
            val file = absPath.substring(dividerIndex + 3)

            val builder = ProcessBuilder("cmd.exe", "/c", "cd $path && java -jar $file")
            builder.redirectErrorStream(true)
            val process = builder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var fullAnswer = ""
            while (true) {
                val line = reader.readLine() ?: break
                fullAnswer += line
                println(line)
            }
        }
    }
}