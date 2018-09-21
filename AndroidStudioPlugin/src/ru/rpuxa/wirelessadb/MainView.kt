package ru.rpuxa.wirelessadb

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import ru.rpuxa.desktop.DesktopActions
import ru.rpuxa.desktop.visual.MainPanel


class MainView : ToolWindowFactory {

    override fun createToolWindowContent(p0: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(MainPanel(PluginActions), TITLE, false)
        toolWindow.contentManager.addContent(content)
    }
}