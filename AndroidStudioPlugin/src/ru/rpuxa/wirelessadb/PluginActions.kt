package ru.rpuxa.wirelessadb

import com.intellij.execution.Executor
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vcs.VcsNotifier.createNotification
import ru.rpuxa.desktop.Actions
import javax.swing.JComponent

object PluginActions : Actions {

    override fun sendMessage(msg: String, panel: JComponent) {
        message(msg, NotificationType.INFORMATION)
    }


    fun errorMessage(msg: String) = message(msg, NotificationType.ERROR)

    fun message(msg: String, type: NotificationType) {
        ApplicationManager.getApplication().invokeLater {
            createNotification(NotificationGroup.balloonGroup(TITLE), TITLE, msg, type, null)

        }
    }
}