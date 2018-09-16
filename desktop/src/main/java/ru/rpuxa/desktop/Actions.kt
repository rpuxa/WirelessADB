package ru.rpuxa.desktop

import javax.swing.JComponent

interface Actions {

    fun sendMessage(msg: String, panel: JComponent)
}