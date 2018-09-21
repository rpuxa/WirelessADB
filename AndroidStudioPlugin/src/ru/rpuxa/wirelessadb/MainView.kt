package ru.rpuxa.wirelessadb

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import ru.rpuxa.desktop.DesktopActions
import org.jetbrains.android.sdk.AndroidSdkUtils
import ru.rpuxa.core.settings.Settings
import ru.rpuxa.desktop.DesktopDeviceInfo
import ru.rpuxa.desktop.DesktopSettings
import ru.rpuxa.desktop.visual.MainPanel


class MainView : ToolWindowFactory {

    override fun createToolWindowContent(p0: Project, toolWindow: ToolWindow) {
        try {
            val contentFactory = ContentFactory.SERVICE.getInstance()
            val content = contentFactory.createContent(
                    MainPanel(PluginActions, PluginSettings(p0), DesktopDeviceInfo, false),
                    "",
                    false
            )
            toolWindow.contentManager.addContent(content)
        } catch (e: Throwable) {
            println()
        }
    }

    class PluginSettings(project: Project) : Settings by DesktopSettings {

        override var adbPath = AndroidSdkUtils.getAdb(project)!!.path!!
    }
}