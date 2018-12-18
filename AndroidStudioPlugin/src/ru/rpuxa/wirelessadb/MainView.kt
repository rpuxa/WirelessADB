package ru.rpuxa.wirelessadb

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import ru.rpuxa.desktop.DesktopActions
import org.jetbrains.android.sdk.AndroidSdkUtils
import ru.rpuxa.core.daemon
import ru.rpuxa.core.internalServer.InternalServerController
import ru.rpuxa.core.settings.Settings
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import ru.rpuxa.desktop.DesktopUtils
import ru.rpuxa.desktop.visual.MainPanel
import java.io.BufferedReader
import java.io.InputStreamReader


class MainView : ToolWindowFactory {

    override fun createToolWindowContent(p0: Project, toolWindow: ToolWindow) {
        try {
            val contentFactory = ContentFactory.SERVICE.getInstance()
            val starter = object : InternalServerController.Starter {

                private val SERVER = "internalServer.jar"

                override fun startServer() {

                    val absPath = Thread.currentThread().contextClassLoader.getResource(SERVER).path

                    val dividerIndex = absPath.lastIndexOf("%5c")
                    val path = absPath.substring(1, dividerIndex)
                    val file = absPath.substring(dividerIndex + 3)

                    val reader = BufferedReader(InputStreamReader(
                            ProcessBuilder("cmd.exe", "/c", "cd $path && java -jar $file")
                                    .redirectErrorStream(true)
                                    .start()
                                    .inputStream
                    ))
                    val thread = Thread {
                        var fullAnswer = ""
                        while (true) {
                            val line = reader.readLine() ?: break
                            fullAnswer += line
                            println(line)
                        }
                    }
                    thread.isDaemon = true
                    thread.start()

                }
            }

            val content = contentFactory.createContent(
                    MainPanel(PluginActions, PluginSettings(p0), DesktopDeviceInfo, starter, false),
                    "",
                    false
            )
            toolWindow.contentManager.addContent(content)
        } catch (e: Throwable) {
            PluginActions.errorMessage(e.toString())
        }
    }

    class PluginSettings(project: Project) : Settings by DesktopSettings {

        override var adbPath = AndroidSdkUtils.getAdb(project)!!.path!!
    }
}