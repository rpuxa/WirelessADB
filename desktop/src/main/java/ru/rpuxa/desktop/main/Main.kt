package ru.rpuxa.desktop.main

import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.desktop.DesktopActions
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import ru.rpuxa.desktop.visual.MainPanel
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.swing.JFrame
import javax.swing.WindowConstants
import kotlin.concurrent.thread


fun main(args: Array<String>) {
    val frame = JFrame("Wireless Adb")
    val starter = object : InternalServerController.Starter {

        private val SERVER_NAME = "internalServer.jar"

        override fun startServer() {

            val input = ClassLoader.getSystemResourceAsStream(SERVER_NAME)

            val folder = File(DesktopDeviceInfo.filesDir)
            if (!folder.exists())
                folder.mkdir()

            val internalServer = File(DesktopDeviceInfo.filesDir, SERVER_NAME)
            FileOutputStream(internalServer).use {
                while (true) {
                    val byte = input.read()
                    if (byte == -1)
                        break
                    it.write(byte)
                }
            }
            input.close()

            val reader = BufferedReader(InputStreamReader(
                    ProcessBuilder(
                            "cmd.exe",
                            "/c",
                            "cd ${internalServer.absolutePath} && java -jar ${internalServer.name}")
                            .redirectErrorStream(true)
                            .start()
                            .inputStream
            ))
            thread(isDaemon = true) {
                var fullAnswer = ""
                while (true) {
                    val line = reader.readLine() ?: break
                    fullAnswer += line
                    println(line)
                }
            }
        }
    }
    frame.add(MainPanel(DesktopActions, DesktopSettings, DesktopDeviceInfo, starter))
    frame.setSize(600, 600)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.isVisible = true
}