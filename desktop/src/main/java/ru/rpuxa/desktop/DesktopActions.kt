package ru.rpuxa.desktop

import javax.swing.JComponent
import javax.swing.JOptionPane

object DesktopActions : Actions {

    override fun sendMessage(msg: String, panel: JComponent) {
        JOptionPane.showMessageDialog(panel, msg)
    }

}