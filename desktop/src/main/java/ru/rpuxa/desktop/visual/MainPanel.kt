package ru.rpuxa.desktop.visual

import ru.rpuxa.core.daemon
import ru.rpuxa.core.internalServer.Device
import ru.rpuxa.core.internalServer.DeviceInfo
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.Settings
import ru.rpuxa.core.settings.SettingsCache
import ru.rpuxa.desktop.Actions
import ru.rpuxa.desktop.DesktopUtils
import java.awt.Component
import java.awt.Dimension
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.swing.*

class MainPanel(actions: Actions,
                settings: Settings,
                info: DeviceInfo,
                showAdbPathRow: Boolean = true) : JPanel() {
    init {
        SettingsCache.load(settings, info)
        SettingsCache.save(settings, info)
    }

    private val mainSwitch = JCheckBox("Enable Wireless Adb")
    private val autoLoading = JCheckBox("Add service to auto-loading")
    private val devicesLabel = JLabel("Type               Name")
    private val disconnectButton = JButton("Disconnect adb from all device")
    private val apkButton = JButton("Install APK")

    private val adbPathPicker = AdbPathPicker(settings, info, actions)
    private val namePicker = NamePicker(settings, info)
    private val deviceListPanel = DeviceListPanel(actions)

    private val listener = object : InternalServerController.InternalServerListener {
        override fun onServerConnected() {
            mainSwitch.isSelected = true
            InternalServerController.setDeviceInfo(info)
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
            if (code == 10061)
                deviceListPanel.fixError10061(device)
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

        add(Box.createVerticalStrut(10))

        add(disconnectButton)

        add(Box.createVerticalStrut(10))

        add(apkButton)
    }

    //Установка листенеров
    init {
        InternalServerController.setListener(listener)

        mainSwitch.addActionListener {
            if (!InternalServerController.isAvailable) {
                InternalServerController.startServer(info, ServerStarter)
            } else {
                deviceListPanel.clear()
                InternalServerController.closeServer()
            }
        }

        disconnectButton.addActionListener {
            ProcessBuilder("cmd.exe", "/c", "cd ${info.adbPath} && adb disconnect").redirectErrorStream(true).start()
        }

        apkButton.addActionListener {
            //            val absPath = getResource("jars\\android.apk").path

        }
    }




    object ServerStarter : InternalServerController.Starter {

        private const val SERVER = "jars\\internalServer.jar"

        override fun startServer() {
            val absPath = DesktopUtils.INSTANCE.getResource(SERVER)

            val dividerIndex = absPath.indexOf("%5c")
            val path = absPath.substring(1, dividerIndex)
            val file = absPath.substring(dividerIndex + 3)

            val reader = BufferedReader(InputStreamReader(
                    ProcessBuilder("cmd.exe", "/c", "cd $path && java -jar $file")
                            .redirectErrorStream(true)
                            .start()
                            .inputStream
            ))
            daemon {
                var fullAnswer = ""
                while (true) {
                    val line = reader.readLine() ?: break
                    fullAnswer += line
                    println(line)
                }
            }
        }
    }
}